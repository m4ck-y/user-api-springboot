package com.m4ck_y.user_api_springboot.domain.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaxIdValidator implements ConstraintValidator<ValidTaxId, String> {

    // persona física: 4 letras + 6 números + 3 alfanuméricos
    private static final String RFC_REGEX = "^[A-ZÑ&]{4}(\\d{6})([A-Z0-9]{3})$";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd");

    @Override
    public void initialize(ValidTaxId constraintAnnotation) {
    }

    @Override
    public boolean isValid(String taxId, ConstraintValidatorContext context) {
        if (taxId == null) {
            return false;
        }

        String rfc = taxId.toUpperCase();

        if (!rfc.matches(RFC_REGEX)) {
            return false;
        }

        // Nota: si la entidad tuviera separado el nombre(first_name, last_name, second..) se podria hacer una mejor validacion
        // O con una api externa

        String datePart = rfc.substring(4, 10);

        return isValidDate(datePart);
    }

    private boolean isValidDate(String datePart) {
        try {
            LocalDate date = LocalDate.parse(datePart, DATE_FORMAT);

            return !date.isAfter(LocalDate.now()); // No aceptar fechas en el futuro
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}

// fuente: https://wwwmat.sat.gob.mx/cs/Satellite?blobcol=urldata&blobkey=id&blobtable=MungoBlobs&blobwhere=1461176322651&ssbinary=true

/* persona fisica
 * {
    "nombre": {
        "apellido": {
            "paterno": [
                {
                    "example": "X",
                    "index": 1,
                    "description": "Letra inicial"
                },
                {
                    "example": "A",
                    "index": 2,
                    "description": "Vocal inicial"
                }
            ],
            "materno": [
                {
                    "example": "X",
                    "index": 3,
                    "description": "Letra inicial"
                }
            ]
        },
        "nombre": [
            {
                "example": "T",
                "index": 4,
                "description": "Letra inicial"
            }
        ]
    },
    "fechaNacimiento": {
        "anio": [
            {
                "example": "9",
                "index": 5,
                "description": "Año de nacimiento (2 dígitos), primer dígito"
            },
            {
                "example": "9",
                "index": 6,
                "description": "Año de nacimiento (2 dígitos), segundo dígito"
            }
        ],
        "mes": [
            {
                "example": "0",
                "index": 7,
                "description": "Mes de nacimiento (2 dígitos), primer dígito"
            },
            {
                "example": "1",
                "index": 8,
                "description": "Mes de nacimiento (2 dígitos), segundo dígito"
            }
        ],
        "dia": [
            {
                "example": "0",
                "index": 9,
                "description": "Día de nacimiento (2 dígitos), primer dígito"
            },
            {
                "example": "1",
                "index": 10,
                "description": "Día de nacimiento (2 dígitos), segundo dígito"
            }
        ]
    },
    "homoclave": {
        "example": "NI4",
        // indexes: 11, 12, 13
        "description": "Tres caracteres diferenciadores únicos. alfanumerico"
    }
}

//se aceptan Ñ
 */