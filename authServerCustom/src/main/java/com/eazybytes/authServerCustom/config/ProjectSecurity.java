package com.eazybytes.authServerCustom.config;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class ProjectSecurity {

    // This is the FIRST security filter chain and handles Authorization Server endpoints.
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            RegisteredClientRepository registeredClientRepository,
            AuthorizationServerSettings authorizationServerSettings) throws Exception {


        // Creates the configuration object responsible for setting up OAuth2 Authorization Server functionality.
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();


        http

                // Makes this filter chain handle only requests belonging to Authorization Server endpoints.
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())

                // Applies the Authorization Server configuration to this HttpSecurity instance.
                .with(authorizationServerConfigurer, authorizationServer ->

                        // Enables OpenID Connect functionality in addition to OAuth2.
                        authorizationServer
                                .oidc(Customizer.withDefaults())
                )

                // Requires authentication for requests handled by this Authorization Server chain.
                .authorizeHttpRequests(authorize ->
                        authorize
                                .anyRequest().authenticated()
                )

                // Defines what should happen when an unauthenticated browser request accesses a protected endpoint.
                .exceptionHandling(exceptions ->

                        // Uses /login as the authentication entry point for HTML requests.
                        exceptions.defaultAuthenticationEntryPointFor(

                                // Redirects the unauthenticated user to Spring Security's /login page.
                                new LoginUrlAuthenticationEntryPoint("/login"),

                                // Applies the login redirect specifically to requests expecting HTML.
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )

                // Also configures this application as a Resource Server capable of validating JWT bearer tokens.
                .oauth2ResourceServer(resourceServer ->

                        // Tells the Resource Server to use JWT-based token validation.
                        resourceServer.jwt(Customizer.withDefaults()));


        // Builds and returns the Authorization Server security filter chain.
        return http.build();
    }


    // This is the SECOND security filter chain and handles requests not matched by the Authorization Server chain.
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {

        http

                // Requires authentication for all normal application requests.
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )

                // Enables form-based login for users who need to authenticate.
                .formLogin(Customizer.withDefaults());


        // Builds and returns the default application security filter chain.
        return http.build();
    }


    // Registers the OAuth2 clients that are allowed to request tokens from this Authorization Server.
    @Bean
    public RegisteredClientRepository registeredClientRepository() {


        // Creates a client registration for the eazybankapi application.
        RegisteredClient clientCredClient =
                RegisteredClient.withId(UUID.randomUUID().toString())

                        // Defines the client_id that the application uses when requesting a token.
                        .clientId("eazybankapi")

                        // Defines the secret used by the client to authenticate itself.
                        .clientSecret("{noop}VxubZgAXyyTq9lGjj3qGvWNsHtE4SqTq")

                        // Requires the client to authenticate using HTTP Basic with client_id and client_secret.
                        .clientAuthenticationMethod(
                                ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                        )

                        // Allows this client to use the Client Credentials OAuth2 grant.
                        .authorizationGrantType(
                                AuthorizationGrantType.CLIENT_CREDENTIALS
                        )

                        // Defines the scopes that this client is allowed to request.
                        .scopes(scopeConfig ->
                                scopeConfig.addAll(
                                        List.of(
                                                // openid is the standard OIDC scope.
                                                OidcScopes.OPENID,

                                                // ADMIN is a custom application scope.
                                                "ADMIN",

                                                // USER is a custom application scope.
                                                "USER"
                                        )
                                )
                        )

                        // Configures how the access token should behave.
                        .tokenSettings(
                                TokenSettings.builder()

                                        // Makes each access token valid for 10 minutes.
                                        .accessTokenTimeToLive(
                                                Duration.ofMinutes(10)
                                        )

                                        // Makes Spring issue a self-contained JWT instead of an opaque token.
                                        .accessTokenFormat(
                                                OAuth2TokenFormat.SELF_CONTAINED
                                        )

                                        // Finishes the TokenSettings configuration.
                                        .build()
                        )

                        // Finishes the RegisteredClient configuration.
                        .build();

        RegisteredClient introspectClient =
                RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("EazyBankIntrospect(Opaque)")
                        .clientSecret("{noop}PutANewRandomSecretOfAround40CharsHere")
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .scopes(scopeConfig ->
                                scopeConfig.addAll(
                                        List.of(OidcScopes.OPENID, "ADMIN", "USER"))
                        )
                        .tokenSettings(
                                TokenSettings.builder()
                                        .accessTokenTimeToLive(
                                                Duration.ofMinutes(10)
                                        )
                                        .accessTokenFormat(
                                                OAuth2TokenFormat.REFERENCE
                                        )
                                        .build()
                        )
                        .build();

        RegisteredClient authCodeClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("eazybankclient")
                .clientSecret("{noop}Qw3rTy6UjMnB9zXcV2pL0sKjHn5TxQqB")
                //the below two lines tell how to sent client credntials either in header(as encoded with header name authentication) or in request body
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                //for refresh token to work we need the below line
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .scope(OidcScopes.OPENID).scope(OidcScopes.EMAIL)
                .clientSettings(ClientSettings.builder().requireProofKey(false).build())
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(10))
                        .refreshTokenTimeToLive(Duration.ofHours(8)).reuseRefreshTokens(false)
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED).build()).build();

        //for pkce even though we mention refresh token it wont be available as we have mentioned "clientAuthenticationMethod(ClientAuthenticationMethod.NONE)"  for refresh token to work we need to mention "CLIENT_SECRET_BASIC"
        //if we mention CLIENT_SECRET_BASIC then we need to mention the clientId and Password like above
        RegisteredClient pkceClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("eazypublicclient")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .scope(OidcScopes.OPENID).scope(OidcScopes.EMAIL)
                .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(10))
                        .refreshTokenTimeToLive(Duration.ofHours(8)).reuseRefreshTokens(false)
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED).build()).build();


        // Stores the registered client in memory instead of a database.
        return new InMemoryRegisteredClientRepository(clientCredClient,introspectClient,authCodeClient,pkceClient);
    }


    // Provides the RSA keys that the Authorization Server uses for signing JWTs.
    @Bean
    public JWKSource<SecurityContext> jwkSource() {


        // Generates a new RSA public/private key pair.
        KeyPair keyPair = generateRsaKey();


        // Extracts the public key from the generated key pair.
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();


        // Extracts the private key from the generated key pair.
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();


        // Converts the Java RSA keys into a Nimbus RSA JWK.
        RSAKey rsaKey = new RSAKey.Builder(publicKey)

                // Adds the private key so the Authorization Server can sign JWTs.
                .privateKey(privateKey)

                // Gives this signing key a unique key ID.
                .keyID(UUID.randomUUID().toString())

                // Finishes creation of the RSA JWK.
                .build();


        // Wraps the RSA JWK inside a JWK Set.
        JWKSet jwkSet = new JWKSet(rsaKey);


        // Makes the JWK Set available to Spring Authorization Server.
        return new ImmutableJWKSet<>(jwkSet);
    }


    // Generates the RSA key pair used for signing and verifying JWTs.
    private static KeyPair generateRsaKey() {

        // Variable that will hold the generated public/private key pair.
        KeyPair keyPair;

        try {

            // Creates a Java KeyPairGenerator capable of generating RSA keys.
            KeyPairGenerator keyPairGenerator =
                    KeyPairGenerator.getInstance("RSA");

            // Configures RSA to use a 2048-bit key.
            keyPairGenerator.initialize(2048);

            // Generates the actual public/private RSA key pair.
            keyPair = keyPairGenerator.generateKeyPair();

        }
        catch (Exception ex) {

            // Converts any key-generation failure into an application startup error.
            throw new IllegalStateException(ex);
        }


        // Returns the generated RSA public/private key pair.
        return keyPair;
    }


    // Creates the JwtDecoder used to decode and validate JWT tokens.
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {

        // Creates a JwtDecoder using the same JWK source configured above.
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }


    // Provides configuration information about this Authorization Server.
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {

        // Creates Authorization Server settings using Spring's default endpoint configuration.
        return AuthorizationServerSettings.builder().build();
    }

    //This is to edit the access token data also we can fetch logged in user details.
    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer()
    {
        return (context) -> {
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                context.getClaims().claims((claims) -> {
                    System.out.println("Principal authorities: " + context.getPrincipal().getAuthorities());
                    if (context.getAuthorizationGrantType().equals(AuthorizationGrantType.CLIENT_CREDENTIALS)) {
                        Set<String> roles = context.getClaims().build().getClaim("scope");
                        claims.put("roles", roles);
                    } else if (context.getAuthorizationGrantType().equals(AuthorizationGrantType.AUTHORIZATION_CODE)) {
                        Set<String> roles = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                                .stream()
                                .map(c -> c.replaceFirst("^ROLE_", ""))
                                .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
                        claims.put("roles", roles);
                        System.out.println("roles"+roles.toString());
                    }
                });
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
