/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2015, 2016 
*/
package quarks.test.runtime.jsoncontrol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import quarks.runtime.jsoncontrol.JsonControlService;

public class JsonControlServiceTest {

    public static interface MyBean {
        void doIt();

        void doString(String s);

        void doInt(int i);

        void doLongDouble(long l, double d);
    }

    public static class MyBeanImpl implements MyBean {

        private boolean doneIt;

        private String doneS;
        private int doneI;
        private long doneL;
        private double doneD;

        public String getDoneS() {
            return doneS;
        }

        public synchronized int getDoneI() {
            return doneI;
        }

        public synchronized long getDoneL() {
            return doneL;
        }

        public synchronized double getDoneD() {
            return doneD;
        }

        @Override
        public synchronized void doIt() {
            doneIt = true;
        }

        synchronized boolean isDoneIt() {
            return doneIt;
        }

        @Override
        public synchronized void doInt(int i) {
            doneI = i;
        }

        @Override
        public synchronized void doString(String s) {
            doneS = s;
        }

        @Override
        public synchronized void doLongDouble(long l, double d) {
            doneL = l;
            doneD = d;
        }
    }

    @Test
    public void testNoArg() throws Exception {
        JsonControlService control = new JsonControlService();

        MyBeanImpl cb1 = new MyBeanImpl();
        MyBeanImpl cb2 = new MyBeanImpl();

        assertFalse(cb1.isDoneIt());
        assertFalse(cb2.isDoneIt());

        control.registerControl("myb", "1", null, MyBean.class, cb1);
        control.registerControl("myb", "2", null, MyBean.class, cb2);

        assertFalse(cb1.isDoneIt());
        assertFalse(cb2.isDoneIt());

        JsonObject req = new JsonObject();
        req.addProperty(JsonControlService.TYPE_KEY, "myb");
        req.addProperty(JsonControlService.ALIAS_KEY, "1");
        req.addProperty(JsonControlService.OP_KEY, "doIt");
        control.controlRequest(req);

        assertTrue(cb1.isDoneIt());
        assertFalse(cb2.isDoneIt());

        req = new JsonObject();
        req.addProperty(JsonControlService.TYPE_KEY, "myb");
        req.addProperty(JsonControlService.ALIAS_KEY, "2");
        req.addProperty(JsonControlService.OP_KEY, "doIt");
        control.controlRequest(req);

        assertTrue(cb1.isDoneIt());
        assertTrue(cb2.isDoneIt());
    }

    @Test
    public void test1ArgString() throws Exception {
        JsonControlService control = new JsonControlService();

        MyBeanImpl cb1 = new MyBeanImpl();
        MyBeanImpl cb2 = new MyBeanImpl();

        assertNull(cb1.getDoneS());
        assertNull(cb2.getDoneS());

        control.registerControl("myb", "1s", null, MyBean.class, cb1);
        control.registerControl("myb", "2s", null, MyBean.class, cb2);

        assertNull(cb1.getDoneS());
        assertNull(cb2.getDoneS());

        JsonObject req = new JsonObject();
        req.addProperty(JsonControlService.TYPE_KEY, "myb");
        req.addProperty(JsonControlService.ALIAS_KEY, "1s");
        req.addProperty(JsonControlService.OP_KEY, "doString");
        JsonArray args = new JsonArray();
        args.add(new JsonPrimitive("ABC"));       
        req.add(JsonControlService.ARGS_KEY, args);
        control.controlRequest(req);

        assertEquals("ABC", cb1.getDoneS());
        assertNull(cb2.getDoneS());

        req = new JsonObject();
        req.addProperty(JsonControlService.TYPE_KEY, "myb");
        req.addProperty(JsonControlService.ALIAS_KEY, "2s");
        req.addProperty(JsonControlService.OP_KEY, "doString");
        args = new JsonArray();
        args.add(new JsonPrimitive("DEF"));       
        req.add(JsonControlService.ARGS_KEY, args);
        control.controlRequest(req);

        assertEquals("ABC", cb1.getDoneS());
        assertEquals("DEF", cb2.getDoneS());
    }
    
    @Test
    public void test1ArgInt() throws Exception {
        JsonControlService control = new JsonControlService();

        MyBeanImpl cb1 = new MyBeanImpl();
        MyBeanImpl cb2 = new MyBeanImpl();

        assertEquals(0, cb1.getDoneI());
        assertEquals(0, cb2.getDoneI());

        control.registerControl("myb", "1i", null, MyBean.class, cb1);
        control.registerControl("myb", "2i", null, MyBean.class, cb2);

        assertEquals(0, cb1.getDoneI());
        assertEquals(0, cb2.getDoneI());

        JsonObject req = new JsonObject();
        req.addProperty(JsonControlService.TYPE_KEY, "myb");
        req.addProperty(JsonControlService.ALIAS_KEY, "1i");
        req.addProperty(JsonControlService.OP_KEY, "doInt");
        JsonArray args = new JsonArray();
        args.add(new JsonPrimitive(726));       
        req.add(JsonControlService.ARGS_KEY, args);
        control.controlRequest(req);

        assertEquals(726, cb1.getDoneI());
        assertEquals(0, cb2.getDoneI());

        req = new JsonObject();
        req.addProperty(JsonControlService.TYPE_KEY, "myb");
        req.addProperty(JsonControlService.ALIAS_KEY, "2i");
        req.addProperty(JsonControlService.OP_KEY, "doInt");
        args = new JsonArray();
        args.add(new JsonPrimitive(2924));       
        req.add(JsonControlService.ARGS_KEY, args);
        control.controlRequest(req);

        assertEquals(726, cb1.getDoneI());
        assertEquals(2924, cb2.getDoneI());
    }
}