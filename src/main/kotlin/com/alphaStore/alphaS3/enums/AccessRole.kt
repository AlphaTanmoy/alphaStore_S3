package com.alphaStore.alphaS3.enums

enum class AccessRole (val nameDescriptor: String)  {
    MERCHANT("Merchants"),
    USER("Users"),
    ADMIN("Admins"),
    DELIVERY_BOY("Delivery Boy"),
//    ADMIN_MERCHANT("Admins & Merchants"),
//    ADMIN_USER("Admins & Users"),
//    ADMIN_DELIVERY_BOY("Admins & Delivery Boy"),
//    ADMIN_MERCHANT_USER("Admins & Merchant & User"),
//    ADMIN_MERCHANT_DELIVERY_BOY("Admins & Merchant & Delivery Boy"),
//    ALL("All")
}