package com.example.sehatin.application.data.response

import com.google.gson.annotations.SerializedName

data class RecipesResponse(

	@field:SerializedName("hits")
	val hits: List<HitsItem>,

	@field:SerializedName("_links")
	val links: Links,

	@field:SerializedName("count")
	val count: Int,

	@field:SerializedName("from")
	val from: Int,

	@field:SerializedName("to")
	val to: Int
)

data class VITB12(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class FAPU(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class SMALL(

	@field:SerializedName("width")
	val width: Int,

	@field:SerializedName("url")
	val url: String,

	@field:SerializedName("height")
	val height: Int
)

data class THUMBNAIL(

	@field:SerializedName("width")
	val width: Int,

	@field:SerializedName("url")
	val url: String,

	@field:SerializedName("height")
	val height: Int
)

data class VITARAE(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class CHOLE(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class VITD(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class SUGARAdded(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class FOLDFE(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class DigestItem(

	@field:SerializedName("schemaOrgTag")
	val schemaOrgTag: String,

	@field:SerializedName("total")
	val total: Any,

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("daily")
	val daily: Any,

	@field:SerializedName("hasRDI")
	val hasRDI: Boolean,

	@field:SerializedName("label")
	val label: String,

	@field:SerializedName("tag")
	val tag: String,

	@field:SerializedName("sub")
	val sub: List<SubItem>
)

data class SubItem(

	@field:SerializedName("schemaOrgTag")
	val schemaOrgTag: String,

	@field:SerializedName("total")
	val total: Any,

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("daily")
	val daily: Any,

	@field:SerializedName("hasRDI")
	val hasRDI: Boolean,

	@field:SerializedName("label")
	val label: String,

	@field:SerializedName("tag")
	val tag: String
)

data class CA(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class PROCNT(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class Next(

	@field:SerializedName("href")
	val href: String,

	@field:SerializedName("title")
	val title: String
)

data class IngredientsItem(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("measure")
	val measure: String,

	@field:SerializedName("foodId")
	val foodId: String,

	@field:SerializedName("weight")
	val weight: Any,

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("foodCategory")
	val foodCategory: String,

	@field:SerializedName("food")
	val food: String
)

data class LARGE(

	@field:SerializedName("width")
	val width: Int,

	@field:SerializedName("url")
	val url: String,

	@field:SerializedName("height")
	val height: Int
)

data class FAT(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class ENERCKCAL(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class TOCPHA(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class Self(

	@field:SerializedName("href")
	val href: String,

	@field:SerializedName("title")
	val title: String
)

data class Images(

	@field:SerializedName("REGULAR")
	val rEGULAR: REGULAR,

	@field:SerializedName("SMALL")
	val sMALL: SMALL,

	@field:SerializedName("LARGE")
	val lARGE: LARGE,

	@field:SerializedName("THUMBNAIL")
	val tHUMBNAIL: THUMBNAIL
)

data class THIA(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class CHOCDFNet(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class REGULAR(

	@field:SerializedName("width")
	val width: Int,

	@field:SerializedName("url")
	val url: String,

	@field:SerializedName("height")
	val height: Int
)

data class P(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class VITK1(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class FAMS(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class SUGAR(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class FIBTG(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class FOLFD(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class TotalNutrients(

	@field:SerializedName("VITB6A")
	val vITB6A: VITB6A,

	@field:SerializedName("FAMS")
	val fAMS: FAMS,

	@field:SerializedName("VITC")
	val vITC: VITC,

	@field:SerializedName("CHOCDF")
	val cHOCDF: CHOCDF,

	@field:SerializedName("K")
	val k: K,

	@field:SerializedName("VITD")
	val vITD: VITD,

	@field:SerializedName("FATRN")
	val fATRN: FATRN,

	@field:SerializedName("P")
	val p: P,

	@field:SerializedName("CHOLE")
	val cHOLE: CHOLE,

	@field:SerializedName("ENERC_KCAL")
	val eNERCKCAL: ENERCKCAL,

	@field:SerializedName("FASAT")
	val fASAT: FASAT,

	@field:SerializedName("VITK1")
	val vITK1: VITK1,

	@field:SerializedName("CHOCDF.net")
	val cHOCDFNet: CHOCDFNet,

	@field:SerializedName("MG")
	val mG: MG,

	@field:SerializedName("RIBF")
	val rIBF: RIBF,

	@field:SerializedName("CA")
	val cA: CA,

	@field:SerializedName("FOLFD")
	val fOLFD: FOLFD,

	@field:SerializedName("WATER")
	val wATER: WATER,

	@field:SerializedName("FAPU")
	val fAPU: FAPU,

	@field:SerializedName("NIA")
	val nIA: NIA,

	@field:SerializedName("THIA")
	val tHIA: THIA,

	@field:SerializedName("FIBTG")
	val fIBTG: FIBTG,

	@field:SerializedName("VITB12")
	val vITB12: VITB12,

	@field:SerializedName("TOCPHA")
	val tOCPHA: TOCPHA,

	@field:SerializedName("SUGAR")
	val sUGAR: SUGAR,

	@field:SerializedName("PROCNT")
	val pROCNT: PROCNT,

	@field:SerializedName("FOLDFE")
	val fOLDFE: FOLDFE,

	@field:SerializedName("NA")
	val nA: NA,

	@field:SerializedName("ZN")
	val zN: ZN,

	@field:SerializedName("VITA_RAE")
	val vITARAE: VITARAE,

	@field:SerializedName("FAT")
	val fAT: FAT,

	@field:SerializedName("FOLAC")
	val fOLAC: FOLAC,

	@field:SerializedName("FE")
	val fE: FE,

	@field:SerializedName("SUGAR.added")
	val sUGARAdded: SUGARAdded
)

data class NIA(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class CHOCDF(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class TotalDaily(

	@field:SerializedName("VITB6A")
	val vITB6A: VITB6A,

	@field:SerializedName("VITC")
	val vITC: VITC,

	@field:SerializedName("CHOCDF")
	val cHOCDF: CHOCDF,

	@field:SerializedName("K")
	val k: K,

	@field:SerializedName("VITD")
	val vITD: VITD,

	@field:SerializedName("P")
	val p: P,

	@field:SerializedName("CHOLE")
	val cHOLE: CHOLE,

	@field:SerializedName("ENERC_KCAL")
	val eNERCKCAL: ENERCKCAL,

	@field:SerializedName("FASAT")
	val fASAT: FASAT,

	@field:SerializedName("VITK1")
	val vITK1: VITK1,

	@field:SerializedName("MG")
	val mG: MG,

	@field:SerializedName("RIBF")
	val rIBF: RIBF,

	@field:SerializedName("CA")
	val cA: CA,

	@field:SerializedName("NIA")
	val nIA: NIA,

	@field:SerializedName("THIA")
	val tHIA: THIA,

	@field:SerializedName("FIBTG")
	val fIBTG: FIBTG,

	@field:SerializedName("VITB12")
	val vITB12: VITB12,

	@field:SerializedName("TOCPHA")
	val tOCPHA: TOCPHA,

	@field:SerializedName("PROCNT")
	val pROCNT: PROCNT,

	@field:SerializedName("FOLDFE")
	val fOLDFE: FOLDFE,

	@field:SerializedName("NA")
	val nA: NA,

	@field:SerializedName("ZN")
	val zN: ZN,

	@field:SerializedName("VITA_RAE")
	val vITARAE: VITARAE,

	@field:SerializedName("FAT")
	val fAT: FAT,

	@field:SerializedName("FE")
	val fE: FE
)

data class K(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class Links(

	@field:SerializedName("next")
	val next: Next,

	@field:SerializedName("self")
	val self: Self
)

data class ZN(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class NA(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class VITC(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class HitsItem(

	@field:SerializedName("_links")
	val links: Links,

	@field:SerializedName("recipe")
	val recipe: Recipe
)

data class VITB6A(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class FE(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class FATRN(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class WATER(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class FOLAC(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class MG(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class RIBF(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class FASAT(

	@field:SerializedName("unit")
	val unit: String,

	@field:SerializedName("quantity")
	val quantity: Any,

	@field:SerializedName("label")
	val label: String
)

data class Recipe(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("shareAs")
	val shareAs: String,

	@field:SerializedName("images")
	val images: Images,

	@field:SerializedName("cautions")
	val cautions: List<String>,

	@field:SerializedName("healthLabels")
	val healthLabels: List<String>,

	@field:SerializedName("co2EmissionsClass")
	val co2EmissionsClass: String,

	@field:SerializedName("totalTime")
	val totalTime: Any,

	@field:SerializedName("mealType")
	val mealType: List<String>,

	@field:SerializedName("label")
	val label: String,

	@field:SerializedName("source")
	val source: String,

	@field:SerializedName("calories")
	val calories: Any,

	@field:SerializedName("totalCO2Emissions")
	val totalCO2Emissions: Any,

	@field:SerializedName("cuisineType")
	val cuisineType: List<String>,

	@field:SerializedName("uri")
	val uri: String,

	@field:SerializedName("url")
	val url: String,

	@field:SerializedName("totalNutrients")
	val totalNutrients: TotalNutrients,

	@field:SerializedName("dietLabels")
	val dietLabels: List<String>,

	@field:SerializedName("dishType")
	val dishType: List<String>,

	@field:SerializedName("yield")
	val yield: Any,

	@field:SerializedName("totalWeight")
	val totalWeight: Any,

	@field:SerializedName("digest")
	val digest: List<DigestItem>,

	@field:SerializedName("ingredients")
	val ingredients: List<IngredientsItem>,

	@field:SerializedName("totalDaily")
	val totalDaily: TotalDaily,

	@field:SerializedName("ingredientLines")
	val ingredientLines: List<String>
)
