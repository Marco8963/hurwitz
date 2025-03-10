public class UnitTest {
    public static void main(String[] args) {
        UnitTest u = new UnitTest();
        u.testMod();
        u.testIsPerfectPower();
    }
    @Test
    public void testMod() {

    }
    @Test
    public void testIsPerfectPower() {
        if(!Util.isPerfectPower(3, -8)) {
            throw new AssertionError();
        }
        if(Util.isPerfectPower(2, -4)) {
            throw new AssertionError();
        }
        System.out.println("Test 'testIsPerfectPower' passed.");
    }
}
