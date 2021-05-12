package com.example.domain.exceptions

import java.lang.Exception

class MissingPathParameterException(): Exception("Required path parameters are missing must be specified.")