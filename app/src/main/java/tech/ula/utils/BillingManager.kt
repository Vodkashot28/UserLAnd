package tech.ula.utils

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams

/**
 * When using this class:
 * - Call `queryPurchases()` in your Activity's onResume() method
 * - Call `startPurchaseFlow()` when one of your in-app products is clicked on
 * - Call `destroy()` in your Activity's onDestroy() method
 */
class BillingManager(
    private val activity: Activity,
    private val onEntitledSubPurchases: (List<Purchase>) -> Unit,
    private val onEntitledInAppPurchases: (List<Purchase>) -> Unit,
    private val onPurchase: (Purchase) -> Unit,
    private val onSubscriptionSupportedChecked: (Boolean) -> Unit
) {

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingResponseCode.OK) {
            purchases?.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    onPurchase(purchase)
                    if (!purchase.isAcknowledged) {
                        val params = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken).build()
                        billingClient.acknowledgePurchase(params) { result ->
                            log("acknowledgePurchase result=$result")
                        }
                    }
                }
            }
        } else {
            log("onPurchasesUpdated responseCode=${billingResult.responseCode}")
        }
    }

    private val productDetailsMap = HashMap<String, ProductDetails>()

    private val billingClient: BillingClient = BillingClient.newBuilder(activity)
        .enablePendingPurchases()
        .setListener(purchasesUpdatedListener)
        .build()

    private var isBillingServiceConnected = false

    init {
        startServiceConnection {
            onSubscriptionSupportedChecked(isSubscriptionPurchaseSupported())
            querySubPurchases()
            queryInAppPurchases()
            queryProductDetails(
                Sku.SUBS_LIST, ProductType.SUBS
            ) { details -> details.forEach { productDetailsMap[it.productId] = it } }
            queryProductDetails(
                Sku.INAPP_LIST, ProductType.INAPP
            ) { details -> details.forEach { productDetailsMap[it.productId] = it } }
        }
    }

    fun querySubPurchases() {
        if (!isSubscriptionPurchaseSupported()) return
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS).build()
        ) { result, purchases ->
            if (result.responseCode == BillingResponseCode.OK)
                onEntitledSubPurchases(purchases)
            else log("querySubPurchases error: $result")
        }
    }

    fun queryInAppPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(ProductType.INAPP).build()
        ) { result, purchases ->
            if (result.responseCode == BillingResponseCode.OK)
                onEntitledInAppPurchases(purchases)
            else log("queryInAppPurchases error: $result")
        }
    }

    fun startPurchaseFlow(productId: String) {
        val details = productDetailsMap[productId] ?: return
        startServiceConnection {
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .apply {
                    // Subscriptions require an offer token
                    details.subscriptionOfferDetails?.firstOrNull()?.offerToken?.let {
                        setOfferToken(it)
                    }
                }
                .build()
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))
                .build()
            billingClient.launchBillingFlow(activity, flowParams)
        }
    }

    fun destroy() {
        if (billingClient.isReady) billingClient.endConnection()
    }

    private fun startServiceConnection(task: () -> Unit) {
        if (isBillingServiceConnected) {
            task()
        } else {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        isBillingServiceConnected = true
                        task()
                    }
                }
                override fun onBillingServiceDisconnected() {
                    isBillingServiceConnected = false
                }
            })
        }
    }

    private fun queryProductDetails(productIds: List<String>, type: String, onSuccess: (List<ProductDetails>) -> Unit) {
        val products = productIds.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(type)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder().setProductList(products).build()
        billingClient.queryProductDetailsAsync(params) { result, detailsList ->
            if (result.responseCode == BillingResponseCode.OK) onSuccess(detailsList)
            else log("queryProductDetails error: $result")
        }
    }

    private fun isSubscriptionPurchaseSupported(): Boolean {
        val response = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        return response.responseCode == BillingResponseCode.OK
    }

    private fun log(message: String) = Log.d("BillingManager", message)

    object Sku {
        const val US1_ONETIME = "1us_onetime"
        const val US5_ONETIME = "5us_onetime"
        const val US10_ONETIME = "10us_onetime"
        const val US20_ONETIME = "20us_onetime"
        const val US1_MONTHLY = "1us_monthly"
        const val US5_MONTHLY = "5us_monthly"
        const val US10_MONTHLY = "10us_monthly"
        const val US20_MONTHLY = "20us_monthly"
        const val US1_YEARLY = "1us_yearly"
        const val US5_YEARLY = "5us_yearly"
        const val US10_YEARLY = "10us_yearly"
        const val US20_YEARLY = "20us_yearly"

        val SUBS_LIST = listOf(US1_MONTHLY, US5_MONTHLY, US10_MONTHLY, US20_MONTHLY,
            US1_YEARLY, US5_YEARLY, US10_YEARLY, US20_YEARLY)
        val INAPP_LIST = listOf(US1_ONETIME, US5_ONETIME, US10_ONETIME, US20_ONETIME)
    }
}
