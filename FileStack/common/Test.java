package common;

public class Test {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String path = "/wrk/seg_arch/raychen/";
        String[] pathComponents = path.split("/");
        for (String component : pathComponents) {
            if (!component.isEmpty())
                System.out.println(component);
        }
    }

}
