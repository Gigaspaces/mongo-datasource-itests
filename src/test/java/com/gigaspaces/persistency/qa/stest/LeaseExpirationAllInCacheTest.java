package com.gigaspaces.persistency.qa.stest;

import com.j_spaces.core.LeaseContext;
import com.gigaspaces.persistency.qa.model.PojoSupportsLeaseExpiration;
import com.gigaspaces.persistency.qa.model.PojoWithPrimitive;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
/**
 * @author Svitlana_Pogrebna
 *
 */
public class LeaseExpirationAllInCacheTest extends AbstractSystemTestUnit {

    @Override
    public void test() {
        say("Lease Expiration test started ...");
        
        int[] pojoIds = {1, 2, 3};
        
        List<PojoSupportsLeaseExpiration> pojos1 = new ArrayList<PojoSupportsLeaseExpiration>(pojoIds.length);
        List<PojoWithPrimitive> pojos2 = new ArrayList<PojoWithPrimitive>(pojoIds.length);
        
        for (int i = 0; i < pojoIds.length; i++) {
            pojos1.add(createPojoWithLease(pojoIds[i]));
            pojos2.add(createPojoWithPrimitive(pojoIds[i]));
        }
        
        final long lease1 = 120000;
        List<Object> pojos = new ArrayList<Object>(pojos1);
        pojos.addAll(pojos2);
        gigaSpace.writeMultiple(pojos.toArray(), lease1);

        waitForEmptyReplicationBacklog(gigaSpace);
        
        PojoSupportsLeaseExpiration[] result1 = gigaSpace.readMultiple(new PojoSupportsLeaseExpiration());
        validateLeaseExpiration(result1, pojoIds, lease1);

        restartPuGscs(testPU, true);
          
        for (int i = 0; i < pojoIds.length; i++) {
            PojoSupportsLeaseExpiration pojoResult = gigaSpace.readById(PojoSupportsLeaseExpiration.class, pojoIds[i], pojoIds[i], 1000);
            PojoSupportsLeaseExpiration expected = pojos1.get(i);
            validatePojoWithLease(pojoResult, expected, lease1);
        }
        
        for (int i = 0; i < pojoIds.length; i++) {
            PojoWithPrimitive pojoResult = gigaSpace.readById(PojoWithPrimitive.class, pojoIds[i], pojoIds[i], 1000);
            PojoWithPrimitive expected = pojos2.get(i);
            assertEquals(expected, pojoResult);
            assertNotNull(pojoResult);
        }
        
        PojoSupportsLeaseExpiration pojoToExpire = pojos1.get(0);
        final long lease2 = 1000;
        LeaseContext<PojoSupportsLeaseExpiration> leaseContext = gigaSpace.write(pojoToExpire, lease2);
        waitForEmptyReplicationBacklog(gigaSpace);
        
        assertTrue(waitForLeaseExpiration(leaseContext, lease2));
        try {
            Thread.sleep(3000); // wait for triggering lease expiration event
        } catch (InterruptedException e) {
        };
        
        restartPuGscs(testPU, false);
        
        for (int i = 1; i < pojoIds.length; i++) {
            PojoSupportsLeaseExpiration pojoResult = gigaSpace.readById(PojoSupportsLeaseExpiration.class, pojoIds[i], pojoIds[i], 1000);
            PojoSupportsLeaseExpiration expected = pojos1.get(i);
            validatePojoWithLease(pojoResult, expected, lease1);
        }
        
        assertNull(gigaSpace.readIfExistsById(PojoSupportsLeaseExpiration.class, pojoToExpire.getId(), pojoToExpire.getId(), 1000));
        
        say("Lease Expiration test finished ...");
    }
    
    private boolean waitForLeaseExpiration(LeaseContext<?> leaseContext, long expectedLease) {
        do {
            try {
                Thread.sleep(1000);
                expectedLease -= 1000;
            } catch (InterruptedException e) {
            }
            long expiredDue = leaseContext.getExpiration() - System.currentTimeMillis();
            if (expiredDue <= 0) {
                return true;
            }
        } while(expectedLease > 0);
        return false;
    }
    
    private void validatePojoWithLease(PojoSupportsLeaseExpiration actual, PojoSupportsLeaseExpiration expected, long lease) {
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertTrue(actual.getLease() > 0);
        assertTrue(actual.getLease() < lease + System.currentTimeMillis());
    }
    
    private void validateLeaseExpiration(PojoSupportsLeaseExpiration[] result, int[] ids, long lease) {
        assertNotNull(result);
        assertEquals(ids.length, result.length);
        for (PojoSupportsLeaseExpiration pojo : result) {
            assertTrue(pojo.getLease() > 0);
            assertTrue(pojo.getLease() < lease + System.currentTimeMillis());
        }
    }

    private PojoSupportsLeaseExpiration createPojoWithLease(int id) {
        PojoSupportsLeaseExpiration pojo = new PojoSupportsLeaseExpiration();
        pojo.setId(id);
        pojo.setDescription("Pojo with lease " + id);
        pojo.setDate(LocalDate.now());
        pojo.setDateTime(LocalDateTime.now());
        pojo.setTime(LocalTime.now());
        pojo.setZonedDateTime(ZonedDateTime.now(ZoneId.of("UTC")));
        return pojo;
    }
    
    private PojoWithPrimitive createPojoWithPrimitive(int id) {
        PojoWithPrimitive pojo = new PojoWithPrimitive();
        pojo.setId(id);
        pojo.setPrimitive(id * 10);
        return pojo;
    }
    
    @Override
    protected String getPUJar() {
        return "/lease-expiration-all-in-cache.jar";
    }
    
    @Override
    protected String getMirrorService() {
        return "/qvc-qa-mirror.jar";
    }
}
