package io.github.bbsk.c5d.config

import io.github.cdimascio.dotenv.dotenv
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService {

    private val log = LoggerFactory.getLogger(JwtService::class.java)

    // Время жизни токена: 24 часа
    val accessTokenExpirationMs: Long = 24L * 60 * 60 * 1000

    private val signingKey: SecretKey by lazy {
        initSigningKey()
    }

    private fun initSigningKey(): SecretKey {
        // Ищем .env файл в корне проекта или текущей директории
        val envDir = listOf(".", "..", "back").firstOrNull { File(it, ".env").exists() || File(it, ".env.example").exists() } ?: "."
        val dotenv = dotenv {
            directory = envDir
            ignoreIfMissing = true
        }

        var secret = dotenv["SECRET_KEY"] ?: System.getenv("SECRET_KEY")

        if (secret.isNullOrBlank()) {
            // Генерируем криптостойкий ключ HS256 через официальный API JJWT
            val newKey = Jwts.SIG.HS256.key().build()
            val encodedSecret = Encoders.BASE64URL.encode(newKey.encoded)
            log.info("SECRET_KEY was empty or not found. Generated new HS256 secret key.")

            // Сохраняем в .env файл, чтобы не перегенерировать при повторном запуске
            persistSecretToEnv(envDir, encodedSecret)
            return newKey
        }

        return try {
            val keyBytes = Decoders.BASE64URL.decode(secret)
            Keys.hmacShaKeyFor(keyBytes)
        } catch (e: Exception) {
            val bytes = secret.toByteArray(StandardCharsets.UTF_8)
            if (bytes.size < 32) {
                Keys.hmacShaKeyFor(secret.padEnd(32, '0').toByteArray(StandardCharsets.UTF_8))
            } else {
                Keys.hmacShaKeyFor(bytes)
            }
        }
    }

    private fun persistSecretToEnv(dirPath: String, secret: String) {
        try {
            var envFile = File(dirPath, ".env")
            if (!envFile.exists()) {
                val exampleFile = File(dirPath, ".env.example")
                if (exampleFile.exists()) {
                    exampleFile.copyTo(envFile, overwrite = false)
                } else {
                    envFile.createNewFile()
                }
            }

            val lines = envFile.readLines()
            var replaced = false
            val updated = lines.map { line ->
                val trimmed = line.trim()
                if (trimmed.startsWith("SECRET_KEY=") || trimmed == "SECRET_KEY") {
                    replaced = true
                    "SECRET_KEY=$secret"
                } else {
                    line
                }
            }.toMutableList()

            if (!replaced) {
                updated.add("SECRET_KEY=$secret")
            }

            envFile.writeText(updated.joinToString("\n"))
            log.info("Saved generated SECRET_KEY to ${envFile.canonicalPath}")
        } catch (e: Exception) {
            log.warn("Could not save SECRET_KEY to .env: ${e.message}")
        }
    }

    fun generateToken(username: String, claims: Map<String, Any> = emptyMap()): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenExpirationMs)

        return Jwts.builder()
            .claims(claims)
            .subject(username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(signingKey)
            .compact()
    }

    fun extractUsername(token: String): String? {
        return extractAllClaims(token)?.subject
    }

    fun isTokenValid(token: String, expectedUsername: String? = null): Boolean {
        val claims = extractAllClaims(token) ?: return false
        val isNotExpired = claims.expiration.after(Date())
        return if (expectedUsername != null) {
            claims.subject == expectedUsername && isNotExpired
        } else {
            isNotExpired
        }
    }

    fun extractAllClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            null
        }
    }
}
