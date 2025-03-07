package utils;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class StripeService {

    private static final String SECRET_KEY = "sk_test_51QxwIsAsFZFj0XFhVKV2japWGDJH0Xk3Gy999FWzPGiYgpAFG41JFII9noo0aICdXapLtNa30LRhdqUz85ow4tNA00VuKjQery"; // Remplace avec ta clé API

    public StripeService() {
        Stripe.apiKey = SECRET_KEY;
    }

    public String createPaymentIntent(int amount, String paymentMethodId) {
        try {
            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount((long) amount * 100)  // Montant en centimes
                    .setCurrency("eur")  // Devise
                    .addPaymentMethodType("card")  // Moyen de paiement
                    .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC);

            if (paymentMethodId != null && !paymentMethodId.isEmpty()) {
                paramsBuilder.setPaymentMethod(paymentMethodId) // Attache la méthode de paiement
                        .setConfirm(true); // Confirme immédiatement si une méthode de paiement est fournie
            }

            PaymentIntentCreateParams params = paramsBuilder.build();
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Debug - Afficher les détails du paiement
            System.out.println("✅ Paiement créé avec succès !");
            System.out.println("🔹 ID : " + paymentIntent.getId());
            System.out.println("🔹 Statut : " + paymentIntent.getStatus());

            return paymentIntent.getClientSecret();  // Retourne le client secret
        } catch (StripeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
