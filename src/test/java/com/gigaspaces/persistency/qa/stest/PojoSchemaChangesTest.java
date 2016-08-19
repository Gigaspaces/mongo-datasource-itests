package com.gigaspaces.persistency.qa.stest;

import javassist.bytecode.annotation.Annotation;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import com.gigaspaces.annotation.pojo.SpaceProperty;
import javassist.CtMethod;
import javassist.CtField;
import com.gigaspaces.persistency.qa.model.PojoToBeReloaded;
import com.gigaspaces.persistency.qa.model.PojoWithPrimitive;
import com.j_spaces.jdbc.driver.GConnection;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.After;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import org.apache.commons.io.FileUtils;
/**
 * @author Svitlana_Pogrebna
 *
 */
public class PojoSchemaChangesTest extends AbstractSystemTestUnit {

    @After
    public void teardown() throws Exception {
        File file = new File(getSpacePuDeployDirectory());
        if (file.exists()) {
            FileUtils.deleteDirectory(file);
        }
    }
    
    @Override
    public void test() {
        say("Pojo Schema Changes test started ...");
        
        int[] pojoIds = {1, 2, 3};
        
        List<PojoToBeReloaded> pojos = new ArrayList<PojoToBeReloaded>(pojoIds.length);
        for (int i = 0; i < pojoIds.length; i++) {
            pojos.add(create(pojoIds[i]));
        }
        gigaSpace.writeMultiple(pojos.toArray());
        waitForEmptyReplicationBacklog(gigaSpace);
        
        ClassPool classPool = ClassPool.getDefault();
        try {
            classPool.insertClassPath(getSpacePuDeployDirectory());
            CtClass pojo = classPool.get(PojoToBeReloaded.class.getName());
            removeProperty(classPool, pojo, "propertyToRemove", classPool.getCtClass("java.lang.Long"));
            removeProperty(classPool, pojo, "primitivePropertyToRemove", CtClass.intType);
            removeProperty(classPool, pojo, "pojoPropertyToRemove", classPool.getCtClass(PojoWithPrimitive.class.getName()));
            addProperty(classPool, pojo, "propertyToAdd", "java.lang.Double", null);
            addProperty(classPool, pojo, "primitivePropertyToAdd", "double", "0.0");
            addProperty(classPool, pojo, "pojoPropertyToAdd", PojoWithPrimitive.class.getName(), null);
            
            pojo.writeFile(getSpacePuDeployDirectory());
        } catch(NotFoundException e) {
            fail(e.getMessage());
        } catch(IOException e) {
            fail(e.getMessage());
        } catch(CannotCompileException e) {
            fail(e.getMessage());
        }
        
        restartPuGscs(testPU, true);
        
        try {
            String url = "jini://*/*/qa-space?groups=qa_group";
            GigaSpace proxy = new GigaSpaceConfigurer(new UrlSpaceConfigurer(url)).gigaSpace();
            GConnection connection = GConnection.getInstance(proxy.getSpace());
            connection.setUseSingleSpace(false);
            String query = "SELECT * FROM " + PojoToBeReloaded.class.getName() + " ORDER BY id";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            
            int i = 0;
            while (rs.next()) {
                assertEquals(pojoIds[i++], rs.getInt("id"));
                validateRemovedProperty(rs, "propertyToRemove");
                validateRemovedProperty(rs, "primitivePropertyToRemove");
                validateRemovedProperty(rs, "pojoPropertyToRemove");
                assertEquals(0.0, rs.getDouble("propertyToAdd"), 0.0);
                assertTrue(rs.wasNull());
                assertNull(rs.getObject("pojoPropertyToAdd"));
                assertEquals(0.0, rs.getDouble("primitivePropertyToAdd"), 0.0);
            }
            assertEquals(pojoIds.length, i);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
     
        say("Pojo Schema Changes test finished ...");
    }

    private void validateRemovedProperty(ResultSet rs, String name) {
        try {
            rs.getObject(name);
            fail("'" + name + "' field has not been removed from schema");
        } catch (SQLException e) {
            // valid
        }
    }
    
    private void addProperty(ClassPool classPool, CtClass pojo, String name, String type, String defaultPrimitiveValue) {
        try {
            CtField field = CtField.make("private " + type + " " + name + ";", pojo);
            pojo.addField(field);
            String propertyName = capitalize(name);
            
            String getterSrc = "public " + type + " get" + propertyName + "() { return " + name + ";}";
            CtMethod getter = CtMethod.make(getterSrc, pojo);
            if (defaultPrimitiveValue != null) {
                ConstPool cp = pojo.getClassFile().getConstPool();
                AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
                Annotation a = new Annotation("com.gigaspaces.annotation.pojo.SpaceProperty", cp);
                a.addMemberValue("nullValue", new javassist.bytecode.annotation.StringMemberValue(defaultPrimitiveValue, cp));
                attr.setAnnotation(a);
                getter.getMethodInfo().addAttribute(attr);
            }
            
            pojo.addMethod(getter);
            
            String setterSrc = "public void set" + propertyName + "(" + type + " " + name + ") { this." + name + " = " + name + ";}";
            CtMethod setter = CtMethod.make(setterSrc, pojo);
            pojo.addMethod(setter);
        } catch (CannotCompileException e) {
            fail(e.getMessage());
        }
    }
    
    private void removeProperty(ClassPool classPool, CtClass pojo, String propertyName, CtClass propertyType) {
        try {
            String name = capitalize(propertyName);
          
            pojo.removeMethod(pojo.getDeclaredMethod("get" + name));
            pojo.removeMethod(pojo.getDeclaredMethod("set" + name, new CtClass[] { propertyType }));
            pojo.removeField(pojo.getDeclaredField(propertyName));
        } catch (NotFoundException e) {
            fail(e.getMessage());
        } 
    }
    
    private String capitalize(String source) {
        return source.replaceFirst("^\\w", String.valueOf(source.charAt(0)).toUpperCase());
    }
    
    private PojoToBeReloaded create(int id) {
        PojoToBeReloaded pojo = new PojoToBeReloaded();
        pojo.setId(id);
        pojo.setPrimitivePropertyToRemove(id * 2);
        pojo.setPropertyToRemove(Long.valueOf(id * 1000));
        
        PojoWithPrimitive pojoWithPrimitive = new PojoWithPrimitive();
        pojoWithPrimitive.setId(id);
        pojoWithPrimitive.setPrimitive(id * 3);
        
        pojo.setPojoPropertyToRemove(pojoWithPrimitive);
        return pojo;
    }
    
    private String getSpacePuDeployDirectory() {
        return System.getenv("GS_HOME") + "/deploy/reload-pojo-schema";
    }
    
    @Override
    protected String getPUJar() {
        return "/reload-pojo-schema.jar";
    }
    
    @Override
    protected String getMirrorService() {
        return "/mongodb-qa-mirror.jar";
    }
}
