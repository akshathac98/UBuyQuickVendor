package com.ubuyquick.vendor.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.CategoryActivity;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.SubCategoryActivity;
import com.ubuyquick.vendor.adapter.CategoryListAdapter;
import com.ubuyquick.vendor.adapter.InventoryProductAdapter;
import com.ubuyquick.vendor.model.InventoryProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryFragment extends Fragment {

    private static final String TAG = "InventoryFragment";

    private Button btn_add_product;
    private Button btn_change_view;

    private String shop_id;
    private ExpandableListView list_categories;
    private CategoryListAdapter categoryListAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shop_id = getArguments().getString("shop_id");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        softdrinks = new ArrayList<>();
        softdrinks.add("Cans");
        softdrinks.add("Bottles");

        juices = new ArrayList<>();
        juices.add("Concentrates");
        juices.add("Squash And Sharbat");
        juices.add("Ayurvedic");
        juices.add("Bottles");

        teacoffee = new ArrayList<>();
        teacoffee.add("Tea");
        teacoffee.add("Coffee");
        teacoffee.add("Dairy Whitener");

        healthenergydrinks = new ArrayList<>();
        healthenergydrinks.add("Chocolate And Health");
        healthenergydrinks.add("Health Drinks");
        healthenergydrinks.add("Energy Drinks");
        healthenergydrinks.add("Water And Soda");

        laundrydetergents = new ArrayList<>();
        laundrydetergents.add("Detergent Powders");
        laundrydetergents.add("Liquid Detergents");
        laundrydetergents.add("Detergent Bars");
        laundrydetergents.add("Laundry Additives");

        dishwashers = new ArrayList<>();
        dishwashers.add("Dishwashing Bars");
        dishwashers.add("Dishwashing Gels");
        dishwashers.add("Scrubbers");

        dishwashinggelspowder = new ArrayList<>();
        dishwashinggelspowder.add("Dishwashing Bars");

        scrubbersclearningaids = new ArrayList<>();
        scrubbersclearningaids.add("Dishwashing Bars");

        cleaners = new ArrayList<>();
        cleaners.add("Toilet Cleaners");
        cleaners.add("Floor Cleaners");
        cleaners.add("Multi-Purpose Cleaner");

        repellents = new ArrayList<>();
        repellents.add("Mosquito Repellents");
        repellents.add("Sprays");
        repellents.add("Creams And Other Repellents");

        poojaneeds = new ArrayList<>();
        poojaneeds.add("Incense Sticks");

        homecarfreshners = new ArrayList<>();
        homecarfreshners.add("Air Fresheners");
        homecarfreshners.add("Car Fresheners");
        homecarfreshners.add("Shoe Care");

        bathbody = new ArrayList<>();
        bathbody.add("Body Wash");
        bathbody.add("Soaps");
        bathbody.add("Handwash");
        bathbody.add("Sanitizer");

        haircare = new ArrayList<>();
        haircare.add("Hair Oil And Others");
        haircare.add("Hair Colour");
        haircare.add("Shampoo");
        haircare.add("Conditioner");

        skincare = new ArrayList<>();
        skincare.add("Body Lotions");
        skincare.add("Talc Powder");
        skincare.add("Body Oils And Others");
        skincare.add("Face Cream");

        oralcare = new ArrayList<>();
        oralcare.add("Tooth Paste");
        oralcare.add("Tooth Brush");
        oralcare.add("Mouth Wash And Others");

        deosperfumes = new ArrayList<>();
        deosperfumes.add("Mens Deo");
        deosperfumes.add("Womens Deo");
        deosperfumes.add("Perfumes");

        facecare = new ArrayList<>();
        facecare.add("Face Wash");
        facecare.add("Face Mask And Bleach");
        facecare.add("Lip Care");
        facecare.add("Face Wipes");
        facecare.add("Face Cream");
        facecare.add("More Face Products");

        femininehygeine = new ArrayList<>();
        femininehygeine.add("Sanitary Care");
        femininehygeine.add("Fem Essentials");
        femininehygeine.add("Hair Removal");

        shavingneeds = new ArrayList<>();
        shavingneeds.add("Razors");
        shavingneeds.add("Cartridges");
        shavingneeds.add("Pre Shave");
        shavingneeds.add("After Shave");

        healthwellness = new ArrayList<>();
        healthwellness.add("Pain Relievers");
        healthwellness.add("Antiseptics");
        healthwellness.add("Other Otcs");
        healthwellness.add("Health Supplements");
        healthwellness.add("Hand And Foot Care");

        cosmetics = new ArrayList<>();
        cosmetics.add("Eye Makeup");
        cosmetics.add("Nail Colors");
        cosmetics.add("Lip Color");

        milkproducts = new ArrayList<>();
        milkproducts.add("Tetra Milk");
        milkproducts.add("Specialty Milk");
        milkproducts.add("Lassi And Flavoured Milk And Milk Shakes");
        milkproducts.add("Cream And Whitener");

        paneercurd = new ArrayList<>();
        paneercurd.add("Paneer And Tofu");
        paneercurd.add("Curd");
        paneercurd.add("Yogurt");

        buttercheese = new ArrayList<>();
        buttercheese.add("Butter");
        buttercheese.add("Cheese");
        buttercheese.add("Spreads");

        breakfastcereal = new ArrayList<>();
        breakfastcereal.add("Flakes");
        breakfastcereal.add("Breakfast Bars");
        breakfastcereal.add("Oats");
        breakfastcereal.add("Muesli");

        breakfastmixes = new ArrayList<>();
        breakfastmixes.add("Mixes");
        breakfastmixes.add("Batter");

        biscuitscookies = new ArrayList<>();
        biscuitscookies.add("Healthy And Digestive");
        biscuitscookies.add("Cream Biscuits And Wafers");
        biscuitscookies.add("Sweet And Salty");
        biscuitscookies.add("Glucose And Marie");

        namkeensnacks = new ArrayList<>();
        namkeensnacks.add("Bhujia And Sev");
        namkeensnacks.add("Namkeen And Mixtures");
        namkeensnacks.add("Papad And Fryums");
        namkeensnacks.add("Local Favourites");

        chipscrisps = new ArrayList<>();
        chipscrisps.add("Potato Chips");
        chipscrisps.add("Crisps And Puffs");
        chipscrisps.add("Tortillas And Nachos");
        chipscrisps.add("Popcorns");

        chocolatecandies = new ArrayList<>();
        chocolatecandies.add("Chocolate Gift Packs");
        chocolatecandies.add("Chocolates");
        chocolatecandies.add("Candies And Chewing Gums");

        sweets = new ArrayList<>();
        sweets.add("Instant Mixes");
        sweets.add("Sweet");

        noodlesvermicelli = new ArrayList<>();
        noodlesvermicelli.add("Instant Noodles");
        noodlesvermicelli.add("Cup Noodles");
        noodlesvermicelli.add("Vermicelli");

        saucesketchups = new ArrayList<>();
        saucesketchups.add("Tomato Ketchup And Sauces");
        saucesketchups.add("Soya And Chilli Sauces");
        saucesketchups.add("Other Sauces");

        jamshoneyspreads = new ArrayList<>();
        jamshoneyspreads.add("Honey");
        jamshoneyspreads.add("Jams And Marmalades");
        jamshoneyspreads.add("Food Spreads");
        jamshoneyspreads.add("Peanut Butter And Mayonnaise");
        jamshoneyspreads.add("Syrups");
        jamshoneyspreads.add("Dressing And Dips");

        pastasoups = new ArrayList<>();
        pastasoups.add("Pasta");
        pastasoups.add("Soup");

        readymademealsmixes = new ArrayList<>();
        readymademealsmixes.add("Breakfast Mixes");
        readymademealsmixes.add("Masala Mixes");

        pickleschutneys = new ArrayList<>();
        pickleschutneys.add("Mango And Lime Pickles");
        pickleschutneys.add("Other Pickles And Chutneys");

        cannedfrozenfood = new ArrayList<>();
        cannedfrozenfood.add("Canned Foods");
        cannedfrozenfood.add("Vegetarian");

        bakingdessertitems = new ArrayList<>();
        bakingdessertitems.add("Baking Ingredients");

        babyfood = new ArrayList<>();
        babyfood.add("Baby Food");

        diaperswipes = new ArrayList<>();
        diaperswipes.add("Diapers");
        diaperswipes.add("Wipes");

        babyskinhaircare = new ArrayList<>();
        babyskinhaircare.add("Bathing Needs");
        babyskinhaircare.add("Body Care And Others");

        groceriesstaples = new ArrayList<String>();
        groceriesstaples.add("Pulses");
        groceriesstaples.add("Atta And Other Flours");

        riceothergrains = new ArrayList<>();
        riceothergrains.add("Basmati");
        riceothergrains.add("Sonamasuri And Kolam");
        riceothergrains.add("Ponni Rice");
        riceothergrains.add("Other Rice");
        riceothergrains.add("Poha");
        riceothergrains.add("Daliya");

        dryfruits = new ArrayList<>();
        dryfruits.add("Almonds And Cashews");
        dryfruits.add("Dates");
        dryfruits.add("Other Dry Fruits");
        dryfruits.add("Nuts And Seeds");

        edibleoils = new ArrayList<>();
        edibleoils.add("Soyabean Oils");
        edibleoils.add("Sunflower Oils");
        edibleoils.add("Mustard Oils");
        edibleoils.add("Health Oils");
        edibleoils.add("Olive Oils");
        edibleoils.add("Til Oil");
        edibleoils.add("Rice Bran Oil And Others");

        gheevanaspathi = new ArrayList<>();
        gheevanaspathi.add("Ghee");

        spices = new ArrayList<>();
        spices.add("Whole Spices");
        spices.add("Powdered Spices");
        spices.add("Ready Masala");
        spices.add("Cooking Paste And Others");

        readymasala = new ArrayList<>();
        readymasala.add("Ready Masala");

        subsubcategories = new HashMap<>();
        subsubcategories.put("Soft Drinks", softdrinks);
        subsubcategories.put("Juice And Concentrates", juices);
        subsubcategories.put("Tea And Coffee", teacoffee);
        subsubcategories.put("Health And Energy Drinks", healthenergydrinks);
        subsubcategories.put("Laundry Detergents", laundrydetergents);
        subsubcategories.put("Dishwashers", dishwashers);
        subsubcategories.put("Dishwashing Gels And Powder", dishwashinggelspowder);
        subsubcategories.put("Scrubbing And Cleaning Aids", scrubbersclearningaids);
        subsubcategories.put("Cleaners", cleaners);
        subsubcategories.put("Repellents", repellents);
        subsubcategories.put("Pooja Needs", poojaneeds);
        subsubcategories.put("Home And Car Fresheners", homecarfreshners);
        subsubcategories.put("Bath And Body", bathbody);
        subsubcategories.put("Hair Care", haircare);
        subsubcategories.put("Skin Care", skincare);
        subsubcategories.put("Oral Care", oralcare);
        subsubcategories.put("Deos And Perfumes", deosperfumes);
        subsubcategories.put("Face Care", facecare);
        subsubcategories.put("Feminine Hygiene", femininehygeine);
        subsubcategories.put("Shaving Needs", shavingneeds);
        subsubcategories.put("Health And Wellness", healthwellness);
        subsubcategories.put("Cosmetics", cosmetics);
        subsubcategories.put("Milk And Milk Products", milkproducts);
        subsubcategories.put("Paneer And Curd", paneercurd);
        subsubcategories.put("Butter And Cheese", buttercheese);
        subsubcategories.put("Breakfast Cereal", breakfastcereal);
        subsubcategories.put("Breakfast Mixes And Batter", breakfastmixes);
        subsubcategories.put("Biscuits And Cookies", biscuitscookies);
        subsubcategories.put("Namkeen And Snacks", namkeensnacks);
        subsubcategories.put("Chips And Crisps", chipscrisps);
        subsubcategories.put("Chocolate And Candies", chocolatecandies);
        subsubcategories.put("Sweets", sweets);
        subsubcategories.put("Noodles And Vermicelli", noodlesvermicelli);
        subsubcategories.put("Sauces And Ketchups", saucesketchups);
        subsubcategories.put("Jams Honey And Spreads", jamshoneyspreads);
        subsubcategories.put("Pasta And Soups", pastasoups);
        subsubcategories.put("Ready Made Meals And Mixes", readymademealsmixes);
        subsubcategories.put("Pickles And Chutneys", pickleschutneys);
        subsubcategories.put("Canned And Frozen Food", cannedfrozenfood);
        subsubcategories.put("Baking And Dessert Items", bakingdessertitems);
        subsubcategories.put("Baby Food", babyfood);
        subsubcategories.put("Diapers And Wipes", diaperswipes);
        subsubcategories.put("Baby Skin And Hair Care", babyskinhaircare);
        subsubcategories.put("Staples", groceriesstaples);
        subsubcategories.put("Rice And Other Grains", riceothergrains);
        subsubcategories.put("Dry Fruits", dryfruits);
        subsubcategories.put("Edible Oils", edibleoils);
        subsubcategories.put("Ghee And Vanaspathi", gheevanaspathi);
        subsubcategories.put("Spices", spices);
        subsubcategories.put("Masala", readymasala);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inventory_fragment, container, false);

        shop_id = getArguments().getString("shop_id");

        list_categories = (ExpandableListView) view.findViewById(R.id.list_category);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Groceries And Staples");
        listDataHeader.add("Beverages");
        listDataHeader.add("Household Needs");
        listDataHeader.add("Personal Care");
        listDataHeader.add("Breakfast And Dairy");
        listDataHeader.add("Biscuits Snacks And Chocolates");
        listDataHeader.add("Noodles Sauces And Instant Food");
        listDataHeader.add("Baby And Kids");

        // Adding child data
        List<String> beverages = new ArrayList<String>();
        beverages.add("Soft Drinks");
        beverages.add("Health And Energy Drinks");
        beverages.add("Tea And Coffee");
        beverages.add("Juice And Concentrates");

        List<String> household = new ArrayList<String>();
        household.add("Laundry Detergents");
        household.add("Dishwashers");
        household.add("Cleaners");
        household.add("Repellents");
        household.add("Pooja Needs");
        household.add("Home And Car Fresheners");

        List<String> personal = new ArrayList<>();
        personal.add("Bath And Body");
        personal.add("Hair Care");
        personal.add("Skin Care");
        personal.add("Oral Care");
        personal.add("Deos And Perfumes");
        personal.add("Face Care");
        personal.add("Feminine Hygiene");
        personal.add("Shaving Needs");
        personal.add("Health And Wellness");
        personal.add("Cosmetics");

        List<String> breakfast = new ArrayList<>();
        breakfast.add("Milk And Milk Products");
        breakfast.add("Paneer And Curd");
        breakfast.add("Butter And Cheese");
        breakfast.add("Breakfast Cereal");
        breakfast.add("Jams Honey And Spreads");
        breakfast.add("Breakfast Mixes And Batter");

        List<String> biscuits = new ArrayList<>();
        biscuits.add("Biscuits And Cookies");
        biscuits.add("Namkeen And Snacks");
        biscuits.add("Chips And Crisps");
        biscuits.add("Chocolate And Candies");
        biscuits.add("Sweets");

        List<String> noodles = new ArrayList<>();
        noodles.add("Noodles And Vermicelli");
        noodles.add("Sauces And Ketchups");
        noodles.add("Pasta And Soups");
        noodles.add("Ready Made Meals And Mixes");
        noodles.add("Pickles And Chutneys");
        noodles.add("Canned And Frozen Food");
        noodles.add("Baking And Dessert Items");

        List<String> baby = new ArrayList<>();
        baby.add("Baby Food");
        baby.add("Diapers And Wipes");
        baby.add("Baby Skin And Hair Care");

        final List<String> staples = new ArrayList<>();
        staples.add("Staples");
        staples.add("Rice And Other Grains");
        staples.add("Dry Fruits");
        staples.add("Edible Oils");
        staples.add("Ghee And Vanaspathi");
        staples.add("Spices");
        staples.add("Masala");

        listDataChild.put(listDataHeader.get(0), staples);
        listDataChild.put(listDataHeader.get(1), beverages);
        listDataChild.put(listDataHeader.get(2), household);
        listDataChild.put(listDataHeader.get(3), personal);
        listDataChild.put(listDataHeader.get(4), breakfast);
        listDataChild.put(listDataHeader.get(5), biscuits);
        listDataChild.put(listDataHeader.get(6), noodles);
        listDataChild.put(listDataHeader.get(7), baby);

        categoryListAdapter = new CategoryListAdapter(getContext(), listDataHeader, listDataChild);

        list_categories.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getContext(),
//                        listDataHeader.get(groupPosition)
//                                + " : "
//                                + listDataChild.get(
//                                listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT)
//                        .show();
                Intent i = new Intent(getContext(), InventorySubCategoryActivity.class);
                i.putExtra("shop_id", shop_id);
                i.putExtra("sub_category", listDataChild.get(
                        listDataHeader.get(groupPosition)).get(childPosition));
                i.putStringArrayListExtra("list", subsubcategories.get(listDataChild.get(
                        listDataHeader.get(groupPosition)).get(childPosition)));
                startActivity(i);
                return false;
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        list_categories.setIndicatorBounds(width - 50, width);

        LayoutInflater layoutInflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.inventory_header, list_categories, false);

        list_categories.setAdapter(categoryListAdapter);
        list_categories.addHeaderView(header);

        btn_add_product = header.findViewById(R.id.btn_add_product);
        btn_change_view = header.findViewById(R.id.btn_change_view);

        btn_change_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getContext(), AddFromActivity.class);
//                i.putExtra("vendor_id", getArguments().getString("vendor_id"));
//                i.putExtra("shop_id", shop_id);
//                startActivity(i);
            }
        });
        return view;
    }

    private Map<String, ArrayList<String>> subsubcategories;

    private ArrayList<String> breakfastmixes;
    private ArrayList<String> softdrinks;
    private ArrayList<String> juices;
    private ArrayList<String> teacoffee;
    private ArrayList<String> healthenergydrinks;

    private ArrayList<String> laundrydetergents;
    private ArrayList<String> dishwashers;
    private ArrayList<String> dishwashinggelspowder;
    private ArrayList<String> scrubbersclearningaids;
    private ArrayList<String> cleaners;
    private ArrayList<String> repellents;
    private ArrayList<String> poojaneeds;
    private ArrayList<String> homecarfreshners;

    private ArrayList<String> bathbody;
    private ArrayList<String> haircare;
    private ArrayList<String> skincare;
    private ArrayList<String> oralcare;
    private ArrayList<String> deosperfumes;
    private ArrayList<String> facecare;
    private ArrayList<String> femininehygeine;
    private ArrayList<String> shavingneeds;
    private ArrayList<String> healthwellness;
    private ArrayList<String> cosmetics;

    private ArrayList<String> milkproducts;
    private ArrayList<String> paneercurd;
    private ArrayList<String> buttercheese;
    private ArrayList<String> breakfastcereal;

    private ArrayList<String> biscuitscookies;
    private ArrayList<String> namkeensnacks;
    private ArrayList<String> chipscrisps;
    private ArrayList<String> chocolatecandies;
    private ArrayList<String> sweets;

    private ArrayList<String> noodlesvermicelli;
    private ArrayList<String> saucesketchups;
    private ArrayList<String> jamshoneyspreads;
    private ArrayList<String> pastasoups;
    private ArrayList<String> readymademealsmixes;
    private ArrayList<String> pickleschutneys;
    private ArrayList<String> cannedfrozenfood;
    private ArrayList<String> bakingdessertitems;

    private ArrayList<String> babyfood;
    private ArrayList<String> diaperswipes;
    private ArrayList<String> babyskinhaircare;

    private ArrayList<String> groceriesstaples;
    private ArrayList<String> riceothergrains;
    private ArrayList<String> dryfruits;
    private ArrayList<String> edibleoils;
    private ArrayList<String> gheevanaspathi;
    private ArrayList<String> spices;
    private ArrayList<String> readymasala;
}
