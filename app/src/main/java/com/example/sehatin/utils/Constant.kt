package com.example.sehatin.utils

import android.Manifest
import android.os.Build

val PERMISSION_LIST_CAMERA =
    mutableListOf(
        Manifest.permission.CAMERA
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

val PERMISSION_LIST_LOCATION =
    mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).toTypedArray()

val key_search = arrayListOf(
    "Easy Recipes",
    "Healthy Recipes",
    "Quick Meals",
    "Dinner Ideas",
    "Chopped Salad Recipes",
    "Healthy Salad Dressings",
    "Easy Vegan Dinners",
    "Vegan Desserts",
    "Sheet Pan Dinners",
    "30-Minute Meals",
    "5-Ingredient Recipes",
    "Slow Cooker Recipes"
)

val FOOD_DESCRIPTION = arrayOf(
    "Perpaduan warna yang semarak menjanjikan ledakan kesegaran, setiap gigitan adalah tarian rasa manis dan asam yang menyenangkan. Teksturnya yang lembut membangkitkan rasa nyaman, seperti pelukan hangat di hari yang sejuk.",
    "Kesempurnaan panggang memunculkan rasa manis yang tersembunyi, kontras yang menyenangkan dengan aroma tanah yang mendasar. Aromanya memenuhi udara, janji kebaikan masakan rumahan.",
    "Kanvas warna cerah menawarkan ledakan kesegaran dengan setiap gigitan. Daging buah yang berair, secara mengejutkan kokoh dengan pelengkap yang lembut, membuat Anda tersenyum.",
    "Kelembutan seperti beludru bertemu dengan sensasi pedas yang mengejutkan, perpaduan harmonis yang membuat Anda menginginkan lebih. Hiasan hijau yang semarak menambah sentuhan kesegaran, mengingatkan pada musim semi.",
    "Berkilau dengan embun, setiap gigitan adalah simfoni rasa - keseimbangan sempurna antara manis dan asam. Daging buah yang keras dengan krim lembut menciptakan kontras tekstur yang menyenangkan.",
    "Secara mengejutkan menyenangkan, kacang-kacangan yang tak terduga berpadu sempurna dengan eksterior yang renyah. Di dalam, hati yang lembut dan empuk menunggu, hadiah yang menenangkan.",
    "Dipanggang sampai berasap dan empuk, kekayaan dari kedalamannya yang tersembunyi mengejutkan. Basis yang creamy, kanvas yang sempurna untuk saus yang tajam, membawa kembali kenangan hangat.",
    "Aroma tanah memenuhi udara, janji kenyamanan yang lembut berenang dalam kolam kenikmatan yang creamy. Ini membangkitkan perasaan rumah dan pertemuan keluarga.",
    "Kesenangan tropis di setiap gigitan, warna-warna cerah menjanjikan ledakan manis musim panas. Daging buah yang berair, ledakan rasa di lidah, membawa Anda ke pantai yang bermandikan sinar matahari.",
    "Disiapkan secara sederhana, rasa yang bersih dan gigitan yang memuaskan menawarkan perubahan yang menyegarkan. Warna hijau yang semarak menambah sentuhan semarak pada piring, pengingat karunia alam.",
    "Keseimbangan sempurna antara manis dan asam, topping menghasilkan isian buah yang berair di bawahnya. Kehangatan yang nyaman bertemu dengan kejutan yang menyenangkan, cita rasa rumah dengan sentuhan modern.",
    "Daging berwarna ungu tua mengejutkan dengan manisnya, kontras yang menyenangkan dengan pelengkap yang creamy. Aroma tanah memenuhi udara, pengingat masa-masa yang lebih sederhana.",
    "Ringan dan menyegarkan, alternatif untuk pasta tradisional ini menawarkan kepuasan yang mengejutkan. Rasa yang lembut memungkinkan saus yang menyertainya untuk bersinar, keseimbangan tekstur dan rasa yang sempurna.",
    "Penuh dengan rasa yang terkonsentrasi, ini menambah kedalaman yang menyenangkan pada hidangan yang creamy. Catatan manis dan asam menambahkan sentuhan kompleksitas, pengingat akan variasi yang ditawarkan alam.",
    "Matang sempurna, daging yang empuk menawarkan semburan manis yang berair. Itu menyegarkan dan dekaden, rasa memanjakan murni.",
    "Gemuk dan penuh dengan jus, setiap gigitan adalah kejutan yang menyenangkan. Rasa manis dan asam menawarkan semburan energi, semangat yang sempurna.",
    "Ditumis dengan bawang putih dan mentega, ini menawarkan tekstur daging yang mengejutkan. Rasa umami yang kaya membangkitkan kenangan akan makanan keluarga yang lezat.",
    "Tekstur renyah bertemu dengan rasa sedikit pahit, kepuasan yang mengejutkan. Warna hijau yang semarak menambah sentuhan semarak pada piring, pengingat pilihan yang sehat.",
    "Pusaran warna oranye dan kuning menawarkan simfoni tekstur halus dan rasa manis yang lembut. Kekayaan creamy membangkitkan rasa nyaman, pelukan hangat untuk jiwa.",
    "Dikukus dengan sempurna, kuntumnya mempertahankan gigitan yang memuaskan. Rasa sedikit pahit, dilengkapi dengan perasan lemon, menawarkan perubahan yang menyegarkan, pengingat pentingnya keseimbangan."
)

fun formatEnglishLabel(label: String) : String {
    return when (label) {
        "Nangka" -> "Jackfruit"
        "JantungPisang" -> "Banana Blossom"
        "Apel" -> "Apple"
        "Melon" -> "Melon"
        "Sawo" -> "Sapodilla"
        "Durian" -> "Durian"
        "bayamMerah" -> "Red Amaranth"
        "Alpukat" -> "Avocado"
        "Salak" -> "Snake Fruit"
        "Belimbing" -> "Belimbing"
        "Buah Naga" -> "Dragon Fruit"
        "Kol Putih" -> "White Cabbage"
        "Duku" -> "Duku"
        "Sawi Putih" -> "Chinese Cabbage"
        "jengkol" -> "Jengkol"
        "DaunMelinjo" -> "Melinjo Leaves"
        "DaunParsley" -> "Parsley"
        "Kelengkeng" -> "Longan"
        "Daun Kucai" -> "Chinese Chives"
        "Nangka Muda" -> "Young Jackfruit"
        "Kacang Panjang" -> "Long Beans"
        "Nanas" -> "Pineapple"
        "Pakis" -> "Fern"
        "Daun Talas" -> "Taro Leaves"
        "Terong Belanda" -> "Eggplant"
        "petai" -> "Petai"
        "Sirsak" -> "Soursop"
        "Daun Pepaya" -> "Papaya Leaves"
        "Rebung" -> "Bamboo Shoots"
        "Kacang Mekah" -> "Lima Beans"
        "Tomat Merah" -> "Red Tomato"
        "Jamur Tiram" -> "Oyster Mushroom"
        "RumputLaut" -> "Seaweed"
        "Timun" -> "Cucumber"
        "Semangka" -> "Watermelon"
        "Daun Seledri" -> "Celery Leaves"
        "Jeruk" -> "Orange"
        "Kangkung" -> "Water Spinach"
        "Markisa" -> "Passion Fruit"
        "Mangga" -> "Mango"
        "Kol Merah" -> "Red Cabbage"
        "Srikaya" -> "Soursop"
        "Jamur Kuping" -> "Wood Ear Fungus"
        "Selada Air" -> "Watercress"
        "Lemon" -> "Lemon"
        "Kelapa" -> "Coconut"
        "Taoge" -> "Bean Sprouts"
        "LabuSiam" -> "Butternut Squash"
        "Bayam Hijau" -> "Spinach"
        "Tomat Muda" -> "Green Tomato"
        "Jeruk Nipis" -> "Lime"
        "Sukun" -> "Breadfruit"
        "Terong" -> "Eggplant"
        "Kecombrang" -> "Torch Ginger"
        "PepayaMuda" -> "Green Papaya"
        "JagungMuda" -> "Baby Corn"
        "Genjer" -> "Water Mimosa"
        "Kecipir" -> "Winged Bean"
        "Rambutan" -> "Rambutan"
        "DaunKubis" -> "Cabbage Leaves"
        "Manggis" -> "Mangosteen"
        "Kesemek" -> "Japanese Persimmon"
        "Matoa" -> "Matoa"
        "Jeruk Bali" -> "Pomelo"
        "Pala" -> "Nutmeg"
        "Buncis" -> "Green Beans"
        "Jambu Air" -> "Rose Apple"
        "Pepaya" -> "Papaya"
        "Mengkudu" -> "Morinda Citrifolia"
        "Jambu Biji" -> "Guava"
        "Selada" -> "Lettuce"
        "Labu Waluh Kuning" -> "Pumpkin"
        "Sawi Hijau" -> "Pak Choi"
        "Melinjo" -> "Melinjo"
        "Pisang" -> "Banana"
        "Kedondong" -> "Ambarella"
        "Anggur" -> "Grapes"
        "Wortel" -> "Carrot"
        else -> "food"
    }

}

