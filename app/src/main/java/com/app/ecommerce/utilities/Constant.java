package com.app.ecommerce.utilities;

import com.app.ecommerce.Config;

public class Constant {

    //API Transactions
    private static final String BASE_URL = Config.ADMIN_PANEL_URL;
    public static final String GET_RECENT_PRODUCT = BASE_URL + "/api/api.php?get_recent";
    public static final String GET_PRODUCT_ID = BASE_URL + "/api/api.php?product_id=";
    public static final String GET_CATEGORY = BASE_URL + "/api/api.php?get_category";
    public static final String GET_CATEGORY_DETAIL = BASE_URL + "/api/api.php?category_id=";
    public static final String GET_HELP = BASE_URL + "/api/api.php?get_help";
    public static final String GET_TAX_CURRENCY = BASE_URL + "/api/api.php?get_tax_currency";
    public static final String GET_SHIPPING = BASE_URL + "/api/api.php?get_shipping";
    public static final String POST_ORDER = BASE_URL + "/api/api.php?post_order";

}
