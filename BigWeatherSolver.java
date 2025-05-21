import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BigWeatherSolver {

    static MyListOfLists adjacencyList;
    static boolean[] visited;
    static MyListOfLists components;

    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter absolute path of input file: ");
        String path = input.nextLine();

        Scanner scanner = new Scanner(new File(path));

        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int bucketCost = scanner.nextInt();
        int bondCost = scanner.nextInt();

        adjacencyList = new MyListOfLists(n + 1);
        visited = new boolean[n + 1];
        components = new MyListOfLists();

        for (int i = 0; i <= n; i++) {
            adjacencyList.addList(new MyList());
        }

        for (int i = 0; i < m; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
        }

        int totalCost = 0;
        long totalWays = 1;

        if (bucketCost <= bondCost) {
            totalCost = n * bucketCost;
            totalWays = 1;
            System.out.println("Minimum Cost: " + totalCost);
            System.out.println("Number of Cheapest Configurations: " + totalWays);
            visualizeSolution(n, bucketCost, bondCost);
        } else {
            for (int i = 1; i <= n; i++) {
                if (!visited[i]) {
                    MyList component = new MyList();
                    dfs(i, component);
                    components.addList(component);
                }
            }

            for (int i = 0; i < components.size(); i++) {
                MyList component = components.get(i);
                int dynosInComponent = component.size();
                int bondsInComponent = dynosInComponent - 1;
                totalCost += bucketCost + bondsInComponent * bondCost;

                long waysToChooseRoot = dynosInComponent;
                long spanningTrees = countSpanningTrees(component);
                totalWays *= waysToChooseRoot * spanningTrees;
            }

            System.out.println("Minimum Cost: " + totalCost);
            System.out.println("Number of Cheapest Configurations: " + totalWays);
            visualizeSolution(n, bucketCost, bondCost);
        }

        scanner.close();
    }

    public static String solve(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);

        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int bucketCost = scanner.nextInt();
        int bondCost = scanner.nextInt();

        adjacencyList = new MyListOfLists(n + 1);
        visited = new boolean[n + 1];
        components = new MyListOfLists();

        for (int i = 0; i <= n; i++) {
            adjacencyList.addList(new MyList());
        }

        for (int i = 0; i < m; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
        }

        int totalCost = 0;
        long totalWays = 1;

        StringBuilder output = new StringBuilder();

        if (bucketCost <= bondCost) {
            totalCost = n * bucketCost;
            totalWays = 1;
            output.append("Minimum Cost: ").append(totalCost).append("\n");
            output.append("Number of Cheapest Configurations: ").append(totalWays).append("\n");
        } else {
            for (int i = 1; i <= n; i++) {
                if (!visited[i]) {
                    MyList component = new MyList();
                    dfs(i, component);
                    components.addList(component);
                }
            }

            for (int i = 0; i < components.size(); i++) {
                MyList component = components.get(i);
                int dynosInComponent = component.size();
                int bondsInComponent = dynosInComponent - 1;
                totalCost += bucketCost + bondsInComponent * bondCost;

                long waysToChooseRoot = dynosInComponent;
                long spanningTrees = countSpanningTrees(component);
                totalWays *= waysToChooseRoot * spanningTrees;
            }

            output.append("Minimum Cost: ").append(totalCost).append("\n");
            output.append("Number of Cheapest Configurations: ").append(totalWays).append("\n");
        }

        scanner.close();
        return output.toString();
    }

    static void dfs(int node, MyList component) {
        visited[node] = true;
        component.add(node);

        MyList neighbors = adjacencyList.get(node);
        for (int i = 0; i < neighbors.size(); i++) {
            int neighbor = neighbors.get(i);
            if (!visited[neighbor]) {
                dfs(neighbor, component);
            }
        }
    }

    static long countSpanningTrees(MyList component) {
        int k = component.size();
        int edgeCount = 0;

        for (int i = 0; i < k; i++) {
            int u = component.get(i);
            for (int j = i + 1; j < k; j++) {
                int v = component.get(j);
                if (adjacencyList.get(u).contains(v)) {
                    edgeCount++;
                }
            }
        }

        if (edgeCount == k * (k - 1) / 2) {
            return (long) Math.pow(k, k - 2);
        }

        if (edgeCount == k - 1) {
            return 1;
        }

        return 1;
    }

    static void visualizeSolution(int n, int bucketCost, int bondCost) {
        System.out.println("\n--- One Cheapest Solution (Sketch View) ---");

        if (bucketCost <= bondCost) {
            for (int i = 1; i <= n; i++) {
                System.out.println("[" + i + "]");
            }
        } else {
            for (int i = 0; i < components.size(); i++) {
                MyList component = components.get(i);
                int root = component.get(0);
                System.out.println("[" + root + "] (root)");
                for (int j = 1; j < component.size(); j++) {
                    System.out.println(" └── [" + component.get(j) + "]");
                }
            }
        }
    }

    static class MyList {
        private int[] elements;
        private int size;

        public MyList() {
            elements = new int[10];
            size = 0;
        }

        public void add(int value) {
            if (size == elements.length) resize();
            elements[size++] = value;
        }

        public int get(int index) {
            return elements[index];
        }

        public int size() {
            return size;
        }

        public boolean contains(int value) {
            for (int i = 0; i < size; i++) {
                if (elements[i] == value) return true;
            }
            return false;
        }

        private void resize() {
            int[] newElements = new int[elements.length * 2];
            for (int i = 0; i < elements.length; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }

    static class MyListOfLists {
        private MyList[] lists;
        private int size;

        public MyListOfLists() {
            lists = new MyList[10];
            size = 0;
        }

        public MyListOfLists(int capacity) {
            lists = new MyList[capacity];
            size = 0;
        }

        public void addList(MyList list) {
            if (size == lists.length) resize();
            lists[size++] = list;
        }

        public MyList get(int index) {
            return lists[index];
        }

        public int size() {
            return size;
        }

        private void resize() {
            MyList[] newLists = new MyList[lists.length * 2];
            for (int i = 0; i < lists.length; i++) {
                newLists[i] = lists[i];
            }
            lists = newLists;
        }
    }
}
