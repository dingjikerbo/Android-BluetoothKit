package com.example;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MyClass {

    static ReferenceQueue<Object> queue = new ReferenceQueue<>();

    public static void main(String[] args) {
        test();
            System.gc();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(queue.poll());
    }

    private static void test() {
        Object obj = new Object();
        PhantomReference ref = new PhantomReference<>(obj, queue);

        System.out.println(ref);

        obj =  null;
        System.gc();

//        System.out.println(queue.poll());
    }



    public static int bstDistance(int[] values, int n, int node1, int node2)
    {
        // WRITE YOUR CODE HERE
        TreeNode root = null;
        for (int k : values) {
            root = buildTree(root, k);
        }
        TreeNode LCA = findLCA(root, node1, node2);
        try {
            return getDistance(LCA, node1) + getDistance(LCA, node2);
        } catch (Exception e) {
            return -1;
        }
    }

    private static int getDistance(TreeNode node, int node1) throws Exception {
        int distance = 0;

        while (node != null) {
            if (node1 < node.value) {
                node = node.left;
                distance++;
            } else if (node1 > node.value) {
                node = node.right;
                distance++;
            } else {
                return distance;
            }
        }

        throw new Exception("Node not exist!");
    }

    private static TreeNode findLCA(TreeNode root, int node1, int node2) {
        while (root != null) {
            if (node1 < root.value && node2 < root.value) {
                root = root.left;
            } else if (node1 > root.value && node2 > root.value) {
                root = root.right;
            } else {
                break;
            }
        }
        return root;
    }

    private static TreeNode buildTree(TreeNode root, int n) {
        if (root == null) {
            return new TreeNode(n);
        } else {
            if (n < root.value) {
                root.left = buildTree(root.left, n);
            } else {
                root.right = buildTree(root.right, n);
            }
            return root;
        }
    }

    private static class TreeNode {
        TreeNode left, right;
        int value;

        TreeNode(int n) {
            this.value = n;
        }
    }

    public static int totalScore(String[] blocks, int n) {
        List<String> list = new LinkedList<String>(Arrays.asList(blocks));
        int lastScore = 0, totalScore = 0;

        for (int i = 0; i < list.size(); ) {
            String s = list.get(i);
            if (s.equals("X")) {
                lastScore *= 2;
                list.set(i++, String.valueOf(lastScore));
            } else if (s.equals("+")) {
                int last2 = (i >= 2 ? Integer.valueOf(list.get(i - 2)) : 0);
                lastScore = last2 + lastScore;
                list.set(i++, String.valueOf(lastScore));
            } else if (s.equals("Z")) {
                list.remove(i);
                if (i >= 1) {
                    list.remove(i - 1);
                    i--;
                }
                lastScore = i >= 1 ? Integer.valueOf(list.get(i - 1)) : 0;
            } else {
                lastScore = Integer.valueOf(s);
                i++;
            }
        }

        for (String s : list) {
            totalScore += Integer.valueOf(s);
        }

        return totalScore;
    }
}
