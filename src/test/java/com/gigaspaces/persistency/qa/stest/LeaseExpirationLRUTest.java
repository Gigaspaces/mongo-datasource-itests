package com.gigaspaces.persistency.qa.stest;

import com.gigaspaces.client.ReadByIdsResult;
import com.gigaspaces.persistency.qa.model.PojoSupportsLeaseExpiration;
import com.gigaspaces.persistency.qa.utils.AssertUtils;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Svitlana_Pogrebna
 *
 */
public class LeaseExpirationLRUTest extends AbstractSystemTestUnit {

    @Override
    public void test() {
        List<PojoSupportsLeaseExpiration> pojos = new ArrayList<PojoSupportsLeaseExpiration>();
        for (int i = 1; i <= 16; i++) {
            pojos.add(createPojoWithLease(i));
        }
        final long lease = 10000;
        gigaSpace.writeMultiple(pojos.toArray(new PojoSupportsLeaseExpiration[] {}), lease);
        waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);

        PojoSupportsLeaseExpiration[] result = gigaSpace.readMultiple(new PojoSupportsLeaseExpiration(), 20);
        AssertUtils.assertEquivalent("Invalid pojos result", pojos, Arrays.asList(result));
        for (PojoSupportsLeaseExpiration pojo : result) {
            assertTrue(pojo.getLease() > 0);
            assertTrue(pojo.getLease() < lease + System.currentTimeMillis());
        }
        
        // readById
        waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);
        PojoSupportsLeaseExpiration readById = gigaSpace.readById(PojoSupportsLeaseExpiration.class, 1);
        assertEquals("Invalid read", pojos.get(0), readById);
        assertTrue(readById.getLease() > 0);
        assertTrue(readById.getLease() < lease + System.currentTimeMillis());

        // readByIds
        waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);
        ReadByIdsResult<PojoSupportsLeaseExpiration> readByIds = gigaSpace.readByIds(PojoSupportsLeaseExpiration.class, new Object[] { 1 });
        PojoSupportsLeaseExpiration[] resultsArray = readByIds.getResultsArray();
        assertEquals("Invalid result count", 1, resultsArray.length);
        assertEquals("Invalid read", pojos.get(0), resultsArray[0]);
        assertTrue(resultsArray[0].getLease() > 0);
        assertTrue(resultsArray[0].getLease() < lease + System.currentTimeMillis());
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
    
    @Override
    protected String getPUJar() {
        return "/lease-expiration-lru.jar";
    }
    
    @Override
    protected String getMirrorService() {
        return "/qvc-qa-mirror.jar";
    }
}
