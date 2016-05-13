--Function: caseframe.eastings_northings_to_lat(numeric, numeric)

-- DROP FUNCTION caseframe.eastings_northings_to_lat(numeric, numeric);

CREATE OR REPLACE FUNCTION caseframe.eastings_northings_to_lat(
    eastings numeric,
    northings numeric)
  RETURNS double precision AS
$BODY$
DECLARE
	a float := 6377563.396;
	b float := 6356256.91;
	F0 float := 0.9996012717;
	E0 float := 400000;
	N0 float := -100000;
	PHI0 float := 0.855211333;
	LAM0 float := -0.034906585;
	north1 integer;
  	east1 integer;
  	aFo float;
  	bFo float;
  	e2 float;
  	n float;
  	initPHI float;
  	nuPL float;
  	rhoPL float;
  	eta2PL float;
  	M float;
  	Et float;
  	vii float;
  	viii float;
  	ix float;
  	lat float;
BEGIN
	/** cast to integer*/
	north1 := CAST(northings as integer);   
	east1 :=CAST(eastings as integer);
      
	aFo := a * F0;
	bFo := b * F0;
	e2 := ((aFo ^ 2) - (bFo ^ 2)) / (aFo ^ 2);
	n := (aFo - bFo) / (aFo + bFo);
	
	InitPHI = caseframe.PHId(North1, N0, aFo, PHI0, n, bFo);
	nuPL := aFo / ((1 - (e2 * (Sin(InitPHI)) ^ 2)) ^ 0.5);
	rhoPL := (nuPL * (1 - e2)) / (1 - (e2 * (Sin(InitPHI)) ^ 2));
	eta2PL = (nuPL / rhoPL) - 1;
	M = caseframe.Marc(bFo, n, PHI0, InitPHI);
	Et = East1 - E0;
	VII = (Tan(InitPHI)) / (2 * nuPL * rhoPL);
	VIII = ((Tan(InitPHI)) / (24 * rhoPL * nuPL ^ 3)) * (5 + (3 * ((Tan(InitPHI)) ^ 2)) + eta2PL - (9 * ((Tan(InitPHI)) ^ 2) * eta2PL));
	IX = ((Tan(InitPHI)) / (720 * rhoPL * nuPL ^ 5)) * (61 + (90 * ((Tan(InitPHI)) ^ 2)) + (45 * ((Tan(InitPHI)) ^ 4)));
	lat = (InitPHI - ((Et ^ 2) * VII) + ((Et ^ 4) * VIII) - ((Et ^ 6) * IX));
	return degrees(lat);
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.eastings_northings_to_lat(numeric, numeric)
   OWNER TO postgres;

-- Function: caseframe.eastings_northings_to_long(numeric, numeric)

-- DROP FUNCTION caseframe.eastings_northings_to_long(numeric, numeric);

CREATE OR REPLACE FUNCTION caseframe.eastings_northings_to_long(
    eastings numeric,
    northings numeric)
  RETURNS double precision AS
$BODY$
DECLARE
	a float = 6377563.396;
	b float = 6356256.91;
	F0 float = 0.9996012717;
	E0 float = 400000;
	N0 float = -100000;
	PHI0 float = 0.855211333;
	LAM0 float = -0.034906585;
	aFo float;
	bFo float;
	e2 float;
	n float;
	initphi float;
	nupl float;
	rhopl float;
	eta2pl float;
	m float;
	et float;
	x float;
	XI float;
	XII float;
	XIIA float;
	long floaT;
	east1 integer;
	north1 integer;
BEGIN
	/** cast to integer*/
	north1 := CAST(northings as integer);   
	east1 :=CAST(eastings as integer);
	
	aFo = a * F0;
	bFo = b * F0;
	e2 = (aFo ^ 2 - bFo ^ 2) / aFo ^ 2;
	n = (aFo - bFo) / (aFo + bFo);
	InitPHI = caseframe.PHId(North1, N0, aFo, PHI0, n, bFo);
	nuPL = aFo / ((1 - (e2 * (Sin(InitPHI)) ^ 2)) ^ 0.5);
	rhoPL = (nuPL * (1 - e2)) / (1 - (e2 * (Sin(InitPHI)) ^ 2));
	eta2PL = (nuPL / rhoPL) - 1;
	M = caseframe.Marc(bFo, n, PHI0, InitPHI);
	Et = East1 - E0;
	X = ((Cos(InitPHI)) ^ -1) / nuPL;
	XI = (((Cos(InitPHI)) ^ -1) / (6 * nuPL ^ 3)) * ((nuPL / rhoPL) + (2 * ((Tan(InitPHI)) ^ 2)));
	XII = (((Cos(InitPHI)) ^ -1) / (120 * nuPL ^ 5)) * (5 + (28 * ((Tan(InitPHI)) ^ 2)) + (24 * ((Tan(InitPHI)) ^ 4)));
	XIIA = (((Cos(InitPHI)) ^ -1) / (5040 * nuPL ^ 7)) * (61 + (662 * ((Tan(InitPHI)) ^ 2)) + (1320 * ((Tan(InitPHI)) ^ 4)) + (720 * ((Tan(InitPHI)) ^ 6)));
	long = (LAM0 + (Et * X) - ((Et ^ 3) * XI) + ((Et ^ 5) * XII) - ((Et ^ 7) * XIIA));
	return degrees(long);
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.eastings_northings_to_long(numeric, numeric)
  OWNER TO postgres;

-- Function: caseframe.marc(double precision, double precision, double precision, double precision)

-- DROP FUNCTION caseframe.marc(double precision, double precision, double precision, double precision);

CREATE OR REPLACE FUNCTION caseframe.marc(
    bfo double precision,
    n double precision,
    p1 double precision,
    p2 double precision)
  RETURNS double precision AS
$BODY$
BEGIN 
  RETURN bFo * (((1 + n + ((5 / 4) * (n ^ 2)) + ((5 / 4) * (n ^ 3))) * (P2 - P1)) - (((3 * n) + (3 * (n ^ 2)) + ((21 / 8) * (n ^ 3)))
              * (Sin(P2 - P1)) * (Cos(P2 + P1))) + ((((15 / 8) * (n ^ 2)) + ((15 / 8) * (n ^ 3))) * (Sin(2 * (P2 - P1)))
              * (Cos(2 * (P2 + P1)))) - (((35 / 24) * (n ^ 3)) * (Sin(3 * (P2 - P1))) * (Cos(3 * (P2 + P1)))));
End$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.marc(double precision, double precision, double precision, double precision)
  OWNER TO postgres;

-- Function: caseframe.phid(integer, double precision, double precision, double precision, double precision, double precision)

-- DROP FUNCTION caseframe.phid(integer, double precision, double precision, double precision, double precision, double precision);

CREATE OR REPLACE FUNCTION caseframe.phid(
    north1 integer,
    n0 double precision,
    afo double precision,
    phi0 double precision,
    n double precision,
    bfo double precision)
  RETURNS double precision AS
$BODY$
DECLARE
	phi1 float;
	phi2 float;
	m float;
BEGIN
	PHI1 = ((North1 - N0) / aFo) + PHI0;
	M = caseframe.Marc(bFo, n, PHI0, PHI1);
	PHI2 = ((North1 - N0 - M) / aFo) + PHI1;
	LOOP
  		IF Abs(North1 - N0 - M) > 0.000000001 THEN
    		EXIT;
  		END IF;
		PHI2 = ((North1 - N0 - M) / aFo) + PHI1;
		M = caseframe.Marc(bFo, n, PHI0, PHI2);
		PHI1 = PHI2;
	end Loop;
	return PHI2;
End$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION caseframe.phid(integer, double precision, double precision, double precision, double precision, double precision)
  OWNER TO postgres;

