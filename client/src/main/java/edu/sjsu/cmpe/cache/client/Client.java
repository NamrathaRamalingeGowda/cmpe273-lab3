package edu.sjsu.cmpe.cache.client;

import java.nio.charset.StandardCharsets;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;

public class Client {

    static String[] data = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};

    static int max_index(long[] weight_array, int len) {
        long x = 0;
        int y = 0;
        for (int i = 0; i < len; i++) {
            if (weight_array[i] > x) {
                x = weight_array[i];
                y = i;
            }
        }
        return y;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cache with:  " + args[0]);
        CacheServiceInterface[] cacheArray = new CacheServiceInterface[3];
        CacheServiceInterface cache0 = new DistributedCacheService(
                "http://localhost:3000");
        CacheServiceInterface cache1 = new DistributedCacheService(
                "http://localhost:3001");
        CacheServiceInterface cache2 = new DistributedCacheService(
                "http://localhost:3002");
        cacheArray[0] = cache0;
        cacheArray[1] = cache1;
        cacheArray[2] = cache2;

        if (args[0].equals("Consistent")) {
           

            for (int i = 0; i < 10; i++) {
                    HashCode hc = Hashing.md5().newHasher()
                            .putLong(i+1)
                            .putString(data[i], StandardCharsets.UTF_8)
                            .hash();
                    int hash = Hashing.consistentHash(hc, 3);
                    if (hash > 2) {
                            System.out.println("wrong hash");
                    } else {
                            cacheArray[hash].put(i+1, data[i]);
                    }
            }
            
            
            for (int i = 0; i < 10; i++) {
                    HashCode hc = Hashing.md5().newHasher().putLong(i+1)
                            .putString(data[i], StandardCharsets.UTF_8)
                            .hash();
                    int hash = Hashing.consistentHash(hc, cacheArray.length);
                    if (hash > 2) {
                            System.out.println("wrong hash");
                    } else {
                            String data = cacheArray[hash].get(i+1);
                            System.out.println("taking from the cache " + hash + 
                                            " : data = " + data + " for the key " + (i + 1));
                    }
            }
        } else {


            long weight[] = new long[3];
            int y = 0;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 3; j++) {
                    weight[j] = Hashing.md5().newHasher()
                                    .putLong(i+1)
                                    .putLong(j)
                                    .putString(data[i], StandardCharsets.UTF_8)
                                    .hash().asLong();
                }
                y = max_index(weight, 3);

                if (y > 2) {
                        System.out.println("wrong hash");
                } else {
                        cacheArray[y].put(i+1, data[i]);
                }
            }
            
            

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 3; j++) {
                    weight[j] = Hashing.md5().newHasher()
                                    .putLong(i+1)
                                    .putLong(j)
                                    .putString(data[i], StandardCharsets.UTF_8)
                                    .hash().asLong();
                }
                y = max_index(weight, 3);

                if (y > 2) {
                    System.out.println("wrong hash");
                } else {
                    String data = cacheArray[y].get(i+1);
                    System.out.println("Taking from the cache " + y + 
                                  " : data = " + data + " for the key" + (i + 1));
                }
            }
        }

    }

}
