package com.example.restaurant_service.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.restaurant_service.dto.PlaceDetails;
import com.example.restaurant_service.dto.PlaceSummary;

@Service
public class PlacesService {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<PlaceSummary> searchPlaces(String query) {
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + query + "&key=" + apiKey;
        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);
        JSONArray results = json.getJSONArray("results");

        List<PlaceSummary> summaries = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject obj = results.getJSONObject(i);
            String placeId = obj.getString("place_id");
            String name = obj.getString("name");
            String address = obj.optString("formatted_address", "N/A");
            summaries.add(new PlaceSummary(placeId, name, address));
        }
        return summaries;
    }
public PlaceDetails getPlaceDetails(String placeId) {
    String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + placeId + "&key=" + apiKey;
    String jsonResponse = restTemplate.getForObject(url, String.class);
    System.out.println("🔍 Raw JSON Response:\n" + jsonResponse); // STEP 1

    JSONObject json = new JSONObject(jsonResponse);
    JSONObject result = json.getJSONObject("result");
    System.out.println("✅ Extracted `result`: " + result.keySet()); // STEP 2

    String name = result.optString("name", "N/A");
    String address = result.optString("formatted_address", "N/A");
    String phone = result.optString("formatted_phone_number", "N/A");

    double latitude = 0.0;
    double longitude = 0.0;
    if (result.has("geometry")) {
        JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
        latitude = location.optDouble("lat", 0.0);
        longitude = location.optDouble("lng", 0.0);
        System.out.println("📍 Location: lat=" + latitude + ", lng=" + longitude); // STEP 3
    }

    String imageUrl = null;
    if (result.has("photos")) {
        JSONArray photos = result.getJSONArray("photos");
        if (photos.length() > 0) {
            String photoRef = photos.getJSONObject(0).optString("photo_reference");
            imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=" + photoRef + "&key=" + apiKey;
            System.out.println("🖼 Image URL: " + imageUrl); // STEP 4
        }
    }

    List<String> openingHours = new ArrayList<>();
    if (result.has("opening_hours") && result.getJSONObject("opening_hours").has("weekday_text")) {
        JSONArray hours = result.getJSONObject("opening_hours").getJSONArray("weekday_text");
        for (int i = 0; i < hours.length(); i++) {
            openingHours.add(hours.getString(i));
        }
        System.out.println("⏰ Opening hours: " + openingHours); // STEP 5
    }

    // 🛠 TYPES Debug
    List<String> types = new ArrayList<>();
    if (result.has("types")) {
        JSONArray typesArray = result.getJSONArray("types");
        System.out.println("🧩 Found types array with " + typesArray.length() + " entries."); // STEP 6
        for (int i = 0; i < typesArray.length(); i++) {
            types.add(typesArray.getString(i));
        }
        System.out.println("✅ Types: " + types); // STEP 7
    } else {
        System.out.println("❌ 'types' not found in result."); // STEP 8
    }

    // ⭐ Rating Debug
    double rating = 0.0;
    if (result.has("rating") && !result.isNull("rating")) {
        rating = result.getDouble("rating");
        System.out.println("⭐ Rating: " + rating); // STEP 9
    } else {
        System.out.println("❌ 'rating' not found or null."); // STEP 10
    }

    // 💰 Price Level Debug
    int priceLevel = -1;
    if (result.has("price_level") && !result.isNull("price_level")) {
        priceLevel = result.getInt("price_level");
        System.out.println("💰 Price Level: " + priceLevel); // STEP 11
    } else {
        System.out.println("❌ 'price_level' not found or null."); // STEP 12
    }

    return new PlaceDetails(
        placeId,
        name,
        address,
        phone,
        latitude,
        longitude,
        imageUrl,
        openingHours,
        types,
        rating,
        priceLevel
    );
}


}
