package com.areahomeschoolgroup.server;

import java.util.Random;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.areahomeschoolgroup.client.GreetingService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	private static final DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	private final Random generator = new Random();
	private static final int NUM_SHARDS = 20;

	public long getCount() {
		long sum = 0;

		Query query = new Query("SimpleCounterShard");
		for (Entity e : ds.prepare(query).asIterable()) {
			sum += (Long) e.getProperty("count");
		}

		return sum;
	}

	@Override
	public String greetServer(String input) throws IllegalArgumentException {
		increment();

		return "Current shard count: " + getCount();
	}

	public void increment() {
		int shardNum = generator.nextInt(NUM_SHARDS);
		Key shardKey = KeyFactory.createKey("SimpleCounterShard", Integer.toString(shardNum));

		Transaction tx = ds.beginTransaction();
		Entity shard;
		try {
			shard = ds.get(tx, shardKey);
			long count = (Long) shard.getProperty("count");
			shard.setUnindexedProperty("count", count + 1L);
		} catch (EntityNotFoundException e) {
			shard = new Entity(shardKey);
			shard.setUnindexedProperty("count", 1L);
		}
		ds.put(tx, shard);
		tx.commit();
	}
}
