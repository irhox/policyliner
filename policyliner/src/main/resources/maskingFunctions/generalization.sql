CREATE OR REPLACE FUNCTION generalize_labname(labname TEXT)
    RETURNS TEXT AS $$
BEGIN
    CASE
        WHEN labname ILIKE 'CBC:%' AND (labname ILIKE '%RED BLOOD%' OR labname ILIKE '%MCH%' OR labname ILIKE '%HEMOGLOBIN%' OR labname ILIKE '%HEMATOCRIT%' OR labname ILIKE '%RDW%' OR labname ILIKE '%MEAN CORPUSCULAR VOLUME%')
            THEN RETURN 'CBC: RED BLOOD CELL TESTS';
        WHEN labname ILIKE 'CBC:%' AND (labname ILIKE '%WHITE BLOOD%' OR labname ILIKE '%LYMPHOCYTES%' OR labname ILIKE '%NEUTROPHILS%' OR labname ILIKE '%MONOCYTES%' OR labname ILIKE '%EOSINOPHILS%' OR labname ILIKE '%BASOPHILS%')
            THEN RETURN 'CBC: WHITE BLOOD CELL TESTS';
        WHEN labname ILIKE 'CBC:%' AND (labname ILIKE '%PLATELET%')
            THEN RETURN 'CBC: PLATELET TESTS';

        WHEN labname ILIKE 'METABOLIC:%' AND (labname ILIKE '%SODIUM%' OR labname ILIKE '%POTASSIUM%' OR labname ILIKE '%CHLORIDE%' OR labname ILIKE '%CARBON DIOXIDE%' OR labname ILIKE '%ANION GAP%')
            THEN RETURN 'METABOLIC: ELECTROLYTES TESTS';
        WHEN labname ILIKE 'METABOLIC:%' AND (labname ILIKE '%BUN%' OR labname ILIKE '%CREATININE%')
            THEN RETURN 'METABOLIC: RENAL FUNCTION TESTS';
        WHEN labname ILIKE 'METABOLIC:%' AND (labname ILIKE '%AST/SGOT%' OR labname ILIKE '%ALT/SGPT%' OR labname ILIKE '%ALK PHOS%' OR labname ILIKE '%BILI TOTAL%' OR labname ILIKE '%ALBUMIN%' OR labname ILIKE '%TOTAL PROTEIN%')
            THEN RETURN 'METABOLIC: LIVER FUNCTION TESTS';
        WHEN labname ILIKE 'METABOLIC:%' AND (labname ILIKE '%GLUCOSE%' OR labname ILIKE '%CALCIUM%')
            THEN RETURN 'METABOLIC: METABOLITES TESTS';

        WHEN labname ILIKE 'URINALYSIS:%' AND (labname ILIKE '%RED BLOOD CELLS%' OR labname ILIKE '%WHITE BLOOD CELLS%')
            THEN RETURN 'URINALYSIS: CELL TESTS';
        WHEN labname ILIKE 'URINALYSIS:%' AND (labname ILIKE '%PH%' OR labname ILIKE '%SPECIFIC GRAVITY%')
            THEN RETURN 'URINALYSIS: CHEMICAL/PHYSICAL TESTS';
        ELSE
            RETURN split_part(labname, ':', 1); -- choose only the first part of the labname, i.e. category;
        END CASE;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION generalize_primarydiagnosiscode(code TEXT)
    RETURNS TEXT AS $$
BEGIN
    -- If string length <= 2, return as-is
    IF length(code) <= 2 THEN
        RETURN code;
    END IF;

    -- Keep first 2 chars, replace the rest with 'X'
    RETURN substring(code FROM 1 FOR 2) ||
           repeat('X', length(code) - 2);
END;
$$ LANGUAGE plpgsql;