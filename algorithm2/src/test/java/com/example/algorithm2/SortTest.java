package com.example.algorithm2;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by chengkuang on 16/6/21.
 */
public class SortTest {


    /**
     * 1. select a pivot, normally the middle one
     * 2. from both ends, swap elements and make all emlements on the left less than
     *  the pivot and all elements on the right greather than the pivot
     * 3. recursively sort left part and right part
     */
    @Test
    public void qiuciTest() {

        int[] x = {9, 2, 4, 7, 3, 7, 10, 100, 1, 80, 5};

//        int[] x = {1, 100, 4, 20, 30, 40, 50};
        System.out.println(Arrays.toString(x));

        int low = 0;
        int high = x.length - 1;
        quickSort(x, low, high);
        System.out.println(Arrays.toString(x));
    }

    private void quickSort(int[] arr, int low, int high) {
        if (arr == null || arr.length == 0) {
            return;
        }

        if (low >= high) {
            return;
        }

        //1. pick the pivot
        int middle = low + (high - low)/2;
        int pivot = arr[middle];

        // 2. make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j) {
            while( arr[i] < pivot) {
                i ++;
            }
            while (arr[j] > pivot) {
                j --;
            }

            if (i <= j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }
        }

        // recursively sort two sub parts
        if (low < j) {
            quickSort(arr, low, j);
        }

        if (high > i) {
            quickSort(arr, i, high);
        }
    }
}
