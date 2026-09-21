package com.example.homework.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/alya")
public class HomeworkController {
    private List<HashMap<String, Object>> drinks = new ArrayList<>();
    private int nextId = 1;

    private void addDrink(String name, String caffeineLevel, double price, double rating) {
        HashMap<String, Object> drink = new LinkedHashMap<>();
        drink.put("id", nextId++);
        drink.put("name", name);
        drink.put("caffeineLevel", caffeineLevel);
        drink.put("price", price);
        drink.put("rating", rating);
        drinks.add(drink);
    }

    public HomeworkController() {
        addDrink("Purrista's Cappuccino", "High", 3.50, 4.8);
        addDrink("Meow-cha Latte", "Medium", 4.00, 4.5);
        addDrink("Espresso Whiskers", "High", 2.50, 4.6);
        addDrink("Iced Cat-mericano", "High", 3.00, 4.3);
        addDrink("Cat Nap Chamomile", "None", 2.75, 4.1);
        addDrink("Cold Brew Claws", "High", 3.75, 4.7);
        addDrink("V60 with Berry Hues", "Medium", 4.25, 4.6);
        addDrink("Chinese Black Tea", "Medium", 3.25, 4.4);
    }

    @GetMapping("/profile")
    public List<String> profile() {
        List<String> response = new ArrayList<>();
        response.add("Developer: Alya");
        response.add("Description: A caffeine-powered dev/CY grad and cat lover who loves animals and nature, dreaming of a quiet farmhouse in Sweden, far away from technology.");
        response.add("Theme: Coffee Shop");
        response.add("Fact: Self-proclaimed caffeine addict. Cappuccino/Spanish Latte is my go-to, matcha and tea when I need something lighter");
        response.add("Fact: I can play the piano");
        response.add("Fact: Half Bahraini, half Indonesian");
        response.add("Fact: If I could, I'd adopt every cat in the world");
        response.add("Favorite Item: Cappuccino");
        response.add("Currently Learning: Advanced Java");
        return response;
    }

    // all Items
    @GetMapping("/drinks")
    public List<HashMap<String, Object>> getAllDrinks() {
        return drinks;
    }

    //drink info by id
    @GetMapping("/drinks/{id}")
    public HashMap<String, Object> getDrinkById(@PathVariable("id") int id) {
        for (HashMap<String, Object> drink : drinks) {
            if ((int) drink.get("id") == id) {
                return drink;
            }
        }
        return null;
    }

    @GetMapping("/drinks/search")
    public List<HashMap<String, Object>> searchDrinks(@RequestParam(value = "name", defaultValue = "None") String name) {
        List<HashMap<String, Object>> res = new ArrayList<>();
        for (HashMap<String, Object> drink : drinks) {
            String drinkName = (String) drink.get("name");
            if (drinkName.toLowerCase().contains(name.toLowerCase())) {
                res.add(drink);
            }
        }
        return res;
    }

    @GetMapping("/drinks/filter")
    public List<HashMap<String, Object>> filterDrinks(@RequestParam(value = "minPrice", defaultValue = "0") double minPrice,
                                                      @RequestParam(value = "maxPrice", defaultValue = "50") double maxPrice) {
        List<HashMap<String, Object>> res = new ArrayList<>();
        for (HashMap<String, Object> drink : drinks) {
            double price = ((Number) drink.get("price")).doubleValue();
            if (price >= minPrice && price <= maxPrice) {
                res.add(drink);
            }
        }
        return res;
        }

    // post - add a new drink
    @PostMapping("/drinks")
    public HashMap<String, Object> createDrink(@RequestBody HashMap<String, Object> newDrink) {
        newDrink.put("id", nextId++);
        drinks.add(newDrink);
        return newDrink;
    }

    @PutMapping("/drinks/{id}")
    public HashMap<String, Object> updateDrink(@PathVariable("id") int id, @RequestBody HashMap<String, Object> newFields) {
        for (HashMap<String, Object> d : drinks) {
            if ((int) d.get("id") == id) {
                d.put("name", newFields.get("name"));
                d.put("caffeineLevel", newFields.get("caffeineLevel"));
                d.put("price", newFields.get("price"));
                d.put("rating", newFields.get("rating"));
                return d;
            }
        }
        return null;
    }

    // delete
    @DeleteMapping("/drinks/{id}")
    public HashMap<String, String> deleteDrink(@PathVariable("id") int id) {
        HashMap<String, String> res = new HashMap<>();
        HashMap<String, Object> toRemove = null;
        for (HashMap<String, Object> d : drinks) {
            if ((int) d.get("id") == id) {
                toRemove = d;
            }
        }
        if (toRemove != null) {
            drinks.remove(toRemove);
            res.put("notif", "Drink with ID " + id + " was deleted.");
        } else {
            res.put("notif", "Drink with ID " + id + " not found.");
        }
        return res;
    }

    @GetMapping("/drinks/stats")
    public HashMap<String, Object> getStats() {
        HashMap<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalDrinks", drinks.size());

        double totalRating = 0;
        double totalPrice = 0;
        HashMap<String, Object> highestRated = drinks.get(0);
        HashMap<String, Integer> countByCaffeineLevel = new HashMap<>();

        for (HashMap<String, Object> drink : drinks) {
            double rating = ((Number) drink.get("rating")).doubleValue();
            double price = ((Number) drink.get("price")).doubleValue();
            double highestRatingSoFar = ((Number) highestRated.get("rating")).doubleValue();

            totalRating += rating;
            totalPrice += price;

            if (rating > highestRatingSoFar) {
                highestRated = drink;
            }

            String caffeineLevel = (String) drink.get("caffeineLevel");
            if (countByCaffeineLevel.containsKey(caffeineLevel)) {
                countByCaffeineLevel.put(caffeineLevel, countByCaffeineLevel.get(caffeineLevel) + 1);
            } else {
                countByCaffeineLevel.put(caffeineLevel, 1);
            }
        }

        stats.put("averageRating", Math.round((totalRating / drinks.size()) * 100.0) / 100.0);
        stats.put("averagePrice", Math.round((totalPrice / drinks.size()) * 100.0) / 100.0);
        stats.put("highestRated", highestRated.get("name"));
        stats.put("countByCaffeineLevel", countByCaffeineLevel);

        return stats;
    }

    // an employee picks ur drink
    @GetMapping("/drinks/surpriseme")
    public HashMap<String, Object> getRandomDrink() {
        Random random = new Random();
        int randomIndex = random.nextInt(drinks.size());
        return drinks.get(randomIndex);
    }

    // Bonus: combines two pieces of info (caffeine level + minimum rating) into one result
    @GetMapping("/drinks/recommend")
    public List<HashMap<String, Object>> recommendDrinks(@RequestParam(value = "caffeineLevel", defaultValue = "High") String caffeineLevel,
                                                         @RequestParam(value = "minRating", defaultValue = "4.0") double minRating) {
        List<HashMap<String, Object>> results = new ArrayList<>();
        for (HashMap<String, Object> drink : drinks) {
            String drinkCaffeine = (String) drink.get("caffeineLevel");
            double rating = ((Number) drink.get("rating")).doubleValue();
            if (drinkCaffeine.equalsIgnoreCase(caffeineLevel) && rating >= minRating) {
                results.add(drink);
            }
        }
        return results;
    }

    //bonus: your caffiene level and the vibes
    @GetMapping("/drinks/vibe")
    public HashMap<String, Object> getVibe(@RequestParam(value = "caffeineLevel", defaultValue = "None") String caffeineLevel) {
        HashMap<String, Object> response = new LinkedHashMap<>();
        response.put("caffeineLevel", caffeineLevel);
        response.put("vibe", getVibeDescription(caffeineLevel));

        List<String> matchingDrinks = new ArrayList<>();
        for (HashMap<String, Object> drink : drinks) {
            String drinkCaffeine = (String) drink.get("caffeineLevel");
            if (drinkCaffeine.equalsIgnoreCase(caffeineLevel)) {
                matchingDrinks.add((String) drink.get("name"));
            }
        }
        response.put("matchingDrinks", matchingDrinks);

        return response;
    }

    // matches the caf level to a vibe (using switch has no equal ignore case)
    private String getVibeDescription(String caffeineLevel) {
        if (caffeineLevel.equalsIgnoreCase("None")) {
            return "Like curling up with a cat for a nap, zero buzz, all cozy.";
        } else if (caffeineLevel.equalsIgnoreCase("Low")) {
            return "A gentle candy rush, a little sweetness and pep, nothing wild.";
        } else if (caffeineLevel.equalsIgnoreCase("Medium")) {
            return "Steady focus mode, enough to keep you sharp without the jitters.";
        } else if (caffeineLevel.equalsIgnoreCase("High")) {
            return "Full send energy, buzzing, wired, ready to conquer the day.";
        } else {
            return "Unknown vibe, try None, Low, Medium, or High.";
        }
    }


}
