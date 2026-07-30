# ==========================================
# ETAPA 1: BUILD (El taller de fabricación)
# ==========================================

# 1. Traemos la maquinaria pesada: Un contenedor con Maven + Java 21 + Alpine Linux.
# Le ponemos el apodo 'builder' para referirnos a esta etapa más adelante.
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

# 2. Creamos y nos situamos en la carpeta '/app' para trabajar con orden y no tirar nada en el sistema raíz.
WORKDIR /app

# 3. Copiamos todos los archivos de tu proyecto local (código Java, pom.xml, etc.) dentro de la carpeta '/app'.
COPY . .

# 4. Le ordenamos a Maven: limpia residuos anteriores, descarga dependencias, compila y empaqueta el ejecutable (.jar).
# Saltamos los tests (-DskipTests) para acelerar el proceso de empaquetado.
RUN mvn clean package -DskipTests


# Docker guarda en caché el resultado de la primera etapa (AS builder)
# Empieza una imagen completamente nueva y limpia desde cero, descartando todo lo pesado de la etapa anterior (Maven, código fuente y residuos).

# ==========================================
# ETAPA 2: RUNTIME (La caja ligera para producción)
# ==========================================

# 5. Desechamos el taller pesado de la Etapa 1 y tomamos una imagen ultraligera que SOLO tiene el entorno de ejecución (JRE Java 21).
# Esto reduce drásticamente el tamaño final de la imagen (de ~800MB a ~150MB).
FROM eclipse-temurin:21-jre-alpine

# 6. Creamos de nuevo la carpeta '/app' dentro de este nuevo contenedor limpio.
WORKDIR /app

# 7. 'Robamos' únicamente el producto final: Copiamos el archivo .jar generado en la etapa 'builder' 
# (/app/target/*.jar) y lo renombrarnos a 'product.jar' dentro de esta nueva imagen.
COPY --from=builder /app/target/*.jar product.jar

# 8. Documentamos que esta aplicación escuchará peticiones en el puerto 8080 (el puerto estándar de Spring Boot).
EXPOSE 8080

# 9. Comando de arranque: Es la orden que se ejecuta al encender el contenedor para poner en marcha el microservicio.
ENTRYPOINT ["java", "-jar", "product.jar"]