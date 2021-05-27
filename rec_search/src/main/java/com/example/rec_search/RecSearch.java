package com.example.rec_search;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class RecSearch implements Runnable{
    int[] m_arr;
    int m_start;
    int m_end;
    int m_num_to_find;

    private static final Logger log = LogManager.getLogger(RecSearch.class.getName());

    public RecSearch(int arr[], int start, int end, int x)
    {
        m_arr = arr;
        m_start = start;
        m_end = end;
        m_num_to_find = x;
    }


    public int SearchInPart(int arr[], int start, int end)
    {
        if (end < start || RecSearchApplication.getIndex_found ().get ()){
            // only for testing, to be able to identify if not founded or stopped
            if(RecSearchApplication.getIndex_found ().get ()){
                return -2;
            }
            return -1;
        }

        if (arr[start] == m_num_to_find) {
            RecSearchApplication.setIndex_found ();
            return start;
        }

        if (arr[end] == m_num_to_find) {
            RecSearchApplication.setIndex_found ();
            return end;
        }

        return SearchInPart(arr, start + 1, end - 1);
    }

    public void run()
    {
        int index = SearchInPart (m_arr,m_start,m_end);
        log.info ("integer found at index: " + index);
//        System.out.println (index);
    }
}