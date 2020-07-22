package core;

public class ErrorHandle {

    public static void handleError(Exception e) {
        System.out.println("[-] error :" + e.getMessage());
    }

    public static void handleError(String e) {
        System.out.println("[-] error :" + e);
    }
}
