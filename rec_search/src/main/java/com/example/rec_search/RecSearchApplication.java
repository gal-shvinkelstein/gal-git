package com.example.rec_search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
public class RecSearchApplication {

	static final int NUM_OF_THREADS = 4;
	public static final AtomicBoolean index_found = new AtomicBoolean (false);

	static void setIndex_found()
	{
		index_found.set(true);
	}
	static AtomicBoolean getIndex_found()
	{
		return index_found;
	}

	public static void main(String[] args) {
		SpringApplication.run(RecSearchApplication.class, args);
		int[] arr = new int[100000];
		Random random = new Random ();

		for (int i = 0; i < arr.length; ++i)
		{
			arr[i] = random.nextInt (1000000);
		}
		int x = arr[2340];


		ExecutorService pool = Executors.newFixedThreadPool(NUM_OF_THREADS);
		System.out.println ("------ Test Case 1 - passing validate value -------");

		for (int i = 0; i < NUM_OF_THREADS; ++i)
		{
			int start = i * (arr.length / NUM_OF_THREADS);
			int end = ((i + 1) * (arr.length / NUM_OF_THREADS)) - 1;
			Runnable SearchPartial = new RecSearch (arr, start, end, x);
			pool.execute (SearchPartial);

		}

		pool.shutdown();
	}

}
