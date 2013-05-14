/*
 * Copyright 2013- Yan Bonnel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.ybo.ybotv.android.lasylist;

import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.modele.Programme;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RatingLoader implements Runnable {


    private static final Map<Integer, Integer> mapOfRatings = new HashMap<Integer, Integer>(){{
        put(1, R.drawable.rating_0_5star);
        put(2, R.drawable.rating_1star);
        put(3, R.drawable.rating_1_5star);
        put(4, R.drawable.rating_2star);
        put(5, R.drawable.rating_2_5star);
        put(6, R.drawable.rating_3star);
        put(7, R.drawable.rating_3_5star);
        put(8, R.drawable.rating_4star);
        put(9, R.drawable.rating_4_5star);
        put(10, R.drawable.rating_5star);
    }};

    private static final int SIZE_OF_CACHE = 100;

    private static final Map<String, Integer> cacheOfResources = Collections.synchronizedMap(new LinkedHashMap<String, Integer>(SIZE_OF_CACHE+1, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
            return size() > SIZE_OF_CACHE;
        }
    });

    public static Integer getResurceInCache(Programme programme) {
        return cacheOfResources.get(programme.getId());
    }

    private Integer getResource(Programme programme) {
        //from web
        try {
            Integer resource = cacheOfResources.get(programme.getId());
            if (resource != null) {
                return resource;
            }
            Float result = programme.getRating();
            if (result != null) {
                resource = mapOfRatings.get(Math.round(result));
                cacheOfResources.put(programme.getId(), resource);
                return resource;
            } else {
                return null;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Programme programme;
    private Runnable afterGetRating;

    public RatingLoader(Programme programme, Runnable afterGetRating) {
        this.programme = programme;
        this.afterGetRating = afterGetRating;
    }

    @Override
    public void run() {
        programme.setRatingResource(getResource(programme));
        if (programme.getRatingResource() != null) {
            afterGetRating.run();
        }
    }
}
