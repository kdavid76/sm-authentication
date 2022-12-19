package com.bkk.sm.mongo.authentication.request

import com.bkk.sm.common.customer.resources.CompanyResource
import com.bkk.sm.common.customer.resources.UserResource

data class RegCompanyRequest(
        val company: CompanyResource,
        val user: UserResource?
)
