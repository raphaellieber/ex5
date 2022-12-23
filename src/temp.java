//import java.util.List;
//import java.util.function.Consumer;
//import java.util.function.Predicate;
//
//public class temp {
//    public static void main(String[] args) {
//        List<Integer> myList = List.of(1,2,3,4,5,6,7,8,9);
//        processNumbers(myList, temp::isPrime, System.out::println);
//    }
//
//    public static void processNumbers(
//            Iterable<Integer> numbers, Predicate<Integer> predicate, Consumer<Integer> consumer) {
//        for (int number : numbers)
//            if (predicate.test(number))
//                consumer.accept(number);
//    }
//
//    private static boolean isPrime(int number) {
//        for (int i = 2; i <= (int) Math.sqrt(number); i++) {
//            if(number % i == 0)
//                return false;
//        }
//        return number > 1;
//    }
//}