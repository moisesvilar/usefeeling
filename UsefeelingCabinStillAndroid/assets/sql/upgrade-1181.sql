DROP TABLE IF EXISTS LOCALES;
CREATE TABLE LOCALES (
IDLOCAL NUMERIC,
TIPOLOCAL TEXT,
NOMBRE TEXT,
DESCRIPCION TEXT,
DIRECCION TEXT,
WEB TEXT,
EMAIL TEXT,
WEB_FACEBOOK TEXT,
TELEFONO TEXT,
TIENEICONO NUMERIC,
ICONODEFECTO TEXT,
LATITUD NUMERIC,
LONGITUD NUMERIC,
VISITAS NUMERIC,
FAVORITOS NUMERIC,
TIENEEVENTOS NUMERIC,
TIENEPROMOS NUMERIC,
PARABEBER NUMERIC,
PARACOMER NUMERIC,
PARASALIR NUMERIC,
AFINIDAD NUMERIC,
ESFAVORITO NUMERIC,
HAYCHECKIN NUMERIC,
PRIMARY KEY(IDLOCAL)
);
CREATE INDEX IX_AFINIDAD ON LOCALES(AFINIDAD ASC);
CREATE INDEX IX_LATITUD ON LOCALES(LATITUD ASC);
CREATE INDEX IX_LONGITUD ON LOCALES(LONGITUD ASC);
DROP TABLE IF EXISTS PROPOSALS;