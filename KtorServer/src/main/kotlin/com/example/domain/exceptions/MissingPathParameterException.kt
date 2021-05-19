package com.example.domain.exceptions

import java.lang.Exception

class MissingPathParameterException(): Exception("Path parameters are missing and must be specified.")