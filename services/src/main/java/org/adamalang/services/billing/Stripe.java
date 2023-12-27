/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.services.billing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.XWWWFormUrl;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;
import org.adamalang.web.client.StringCallbackHttpResponder;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.function.Consumer;

/** https://stripe.com/docs/api */
public class Stripe extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Stripe.class);
  private final FirstPartyMetrics metrics;
  private final WebClientBase base;
  private final String apikey;

  public Stripe(FirstPartyMetrics metrics, WebClientBase base, String apikey) {
    super("stripe", new NtPrincipal("stripe", "service"), true);
    this.metrics = metrics;
    this.base = base;
    this.apikey = apikey;
  }

  public static Stripe build(FirstPartyMetrics metrics, ServiceConfig config, WebClientBase base) throws ErrorCodeException {
    String apikey = config.getDecryptedSecret("apikey");
    return new Stripe(metrics, base, apikey);
  }

  private void invoke(String httpMethod, String uri, ObjectNode request, Callback<String> callback) {
    String body = XWWWFormUrl.encode(request);
    TreeMap<String, String> headers = new TreeMap<>();
    headers.put("Authorization", "Bearer " + apikey);
    String url = "https://api.stripe.com" + uri;
    SimpleHttpRequest req = new SimpleHttpRequest(httpMethod, url, headers, SimpleHttpRequestBody.WRAP(body.getBytes(StandardCharsets.UTF_8)));
    base.executeShared(req, new StringCallbackHttpResponder(LOGGER, metrics.stripe_invoke.start(), callback));
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("service stripe {\n");
    sb.append("  class=\"stripe\";\n");
    sb.append("  ").append(params).append("\n");
    if (!names.contains("apikey")) {
      error.accept("Stripe requires an 'apikey' (and it should be encrypted)");
    }
/** BEGIN[CODEGEN-DEFN] **/
    sb.append("  method<dynamic, dynamic> GetAccount;\n");
    sb.append("  method<dynamic, dynamic> PostAccountLinks;\n");
    sb.append("  method<dynamic, dynamic> GetAccounts;\n");
    sb.append("  method<dynamic, dynamic> PostAccounts;\n");
    sb.append("  method<dynamic, dynamic> DeleteAccountsAccount;\n");
    sb.append("  method<dynamic, dynamic> GetAccountsAccount;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccount;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccountBankAccounts;\n");
    sb.append("  method<dynamic, dynamic> DeleteAccountsAccountBankAccountsId;\n");
    sb.append("  method<dynamic, dynamic> GetAccountsAccountBankAccountsId;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccountBankAccountsId;\n");
    sb.append("  method<dynamic, dynamic> GetAccountsAccountCapabilities;\n");
    sb.append("  method<dynamic, dynamic> GetAccountsAccountCapabilitiesCapability;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccountCapabilitiesCapability;\n");
    sb.append("  method<dynamic, dynamic> GetAccountsAccountExternalAccounts;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccountExternalAccounts;\n");
    sb.append("  method<dynamic, dynamic> DeleteAccountsAccountExternalAccountsId;\n");
    sb.append("  method<dynamic, dynamic> GetAccountsAccountExternalAccountsId;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccountExternalAccountsId;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccountLoginLinks;\n");
    sb.append("  method<dynamic, dynamic> GetAccountsAccountPeople;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccountPeople;\n");
    sb.append("  method<dynamic, dynamic> DeleteAccountsAccountPeoplePerson;\n");
    sb.append("  method<dynamic, dynamic> GetAccountsAccountPeoplePerson;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccountPeoplePerson;\n");
    sb.append("  method<dynamic, dynamic> GetAccountsAccountPersons;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccountPersons;\n");
    sb.append("  method<dynamic, dynamic> DeleteAccountsAccountPersonsPerson;\n");
    sb.append("  method<dynamic, dynamic> GetAccountsAccountPersonsPerson;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccountPersonsPerson;\n");
    sb.append("  method<dynamic, dynamic> PostAccountsAccountReject;\n");
    sb.append("  method<dynamic, dynamic> GetApplePayDomains;\n");
    sb.append("  method<dynamic, dynamic> PostApplePayDomains;\n");
    sb.append("  method<dynamic, dynamic> DeleteApplePayDomainsDomain;\n");
    sb.append("  method<dynamic, dynamic> GetApplePayDomainsDomain;\n");
    sb.append("  method<dynamic, dynamic> GetApplicationFees;\n");
    sb.append("  method<dynamic, dynamic> GetApplicationFeesFeeRefundsId;\n");
    sb.append("  method<dynamic, dynamic> PostApplicationFeesFeeRefundsId;\n");
    sb.append("  method<dynamic, dynamic> GetApplicationFeesId;\n");
    sb.append("  method<dynamic, dynamic> PostApplicationFeesIdRefund;\n");
    sb.append("  method<dynamic, dynamic> GetApplicationFeesIdRefunds;\n");
    sb.append("  method<dynamic, dynamic> PostApplicationFeesIdRefunds;\n");
    sb.append("  method<dynamic, dynamic> GetAppsSecrets;\n");
    sb.append("  method<dynamic, dynamic> PostAppsSecrets;\n");
    sb.append("  method<dynamic, dynamic> PostAppsSecretsDelete;\n");
    sb.append("  method<dynamic, dynamic> GetAppsSecretsFind;\n");
    sb.append("  method<dynamic, dynamic> GetBalance;\n");
    sb.append("  method<dynamic, dynamic> GetBalanceHistory;\n");
    sb.append("  method<dynamic, dynamic> GetBalanceHistoryId;\n");
    sb.append("  method<dynamic, dynamic> GetBalanceTransactions;\n");
    sb.append("  method<dynamic, dynamic> GetBalanceTransactionsId;\n");
    sb.append("  method<dynamic, dynamic> GetBillingPortalConfigurations;\n");
    sb.append("  method<dynamic, dynamic> PostBillingPortalConfigurations;\n");
    sb.append("  method<dynamic, dynamic> GetBillingPortalConfigurationsConfiguration;\n");
    sb.append("  method<dynamic, dynamic> PostBillingPortalConfigurationsConfiguration;\n");
    sb.append("  method<dynamic, dynamic> PostBillingPortalSessions;\n");
    sb.append("  method<dynamic, dynamic> GetCharges;\n");
    sb.append("  method<dynamic, dynamic> PostCharges;\n");
    sb.append("  method<dynamic, dynamic> GetChargesSearch;\n");
    sb.append("  method<dynamic, dynamic> GetChargesCharge;\n");
    sb.append("  method<dynamic, dynamic> PostChargesCharge;\n");
    sb.append("  method<dynamic, dynamic> PostChargesChargeCapture;\n");
    sb.append("  method<dynamic, dynamic> GetChargesChargeDispute;\n");
    sb.append("  method<dynamic, dynamic> PostChargesChargeDispute;\n");
    sb.append("  method<dynamic, dynamic> PostChargesChargeDisputeClose;\n");
    sb.append("  method<dynamic, dynamic> PostChargesChargeRefund;\n");
    sb.append("  method<dynamic, dynamic> GetChargesChargeRefunds;\n");
    sb.append("  method<dynamic, dynamic> PostChargesChargeRefunds;\n");
    sb.append("  method<dynamic, dynamic> GetChargesChargeRefundsRefund;\n");
    sb.append("  method<dynamic, dynamic> PostChargesChargeRefundsRefund;\n");
    sb.append("  method<dynamic, dynamic> GetCheckoutSessions;\n");
    sb.append("  method<dynamic, dynamic> PostCheckoutSessions;\n");
    sb.append("  method<dynamic, dynamic> GetCheckoutSessionsSession;\n");
    sb.append("  method<dynamic, dynamic> PostCheckoutSessionsSessionExpire;\n");
    sb.append("  method<dynamic, dynamic> GetCheckoutSessionsSessionLineItems;\n");
    sb.append("  method<dynamic, dynamic> GetCountrySpecs;\n");
    sb.append("  method<dynamic, dynamic> GetCountrySpecsCountry;\n");
    sb.append("  method<dynamic, dynamic> GetCoupons;\n");
    sb.append("  method<dynamic, dynamic> PostCoupons;\n");
    sb.append("  method<dynamic, dynamic> DeleteCouponsCoupon;\n");
    sb.append("  method<dynamic, dynamic> GetCouponsCoupon;\n");
    sb.append("  method<dynamic, dynamic> PostCouponsCoupon;\n");
    sb.append("  method<dynamic, dynamic> GetCreditNotes;\n");
    sb.append("  method<dynamic, dynamic> PostCreditNotes;\n");
    sb.append("  method<dynamic, dynamic> GetCreditNotesPreview;\n");
    sb.append("  method<dynamic, dynamic> GetCreditNotesPreviewLines;\n");
    sb.append("  method<dynamic, dynamic> GetCreditNotesCreditNoteLines;\n");
    sb.append("  method<dynamic, dynamic> GetCreditNotesId;\n");
    sb.append("  method<dynamic, dynamic> PostCreditNotesId;\n");
    sb.append("  method<dynamic, dynamic> PostCreditNotesIdVoid;\n");
    sb.append("  method<dynamic, dynamic> GetCustomers;\n");
    sb.append("  method<dynamic, dynamic> PostCustomers;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersSearch;\n");
    sb.append("  method<dynamic, dynamic> DeleteCustomersCustomer;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomer;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomer;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerBalanceTransactions;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerBalanceTransactions;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerBalanceTransactionsTransaction;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerBalanceTransactionsTransaction;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerBankAccounts;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerBankAccounts;\n");
    sb.append("  method<dynamic, dynamic> DeleteCustomersCustomerBankAccountsId;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerBankAccountsId;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerBankAccountsId;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerBankAccountsIdVerify;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerCards;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerCards;\n");
    sb.append("  method<dynamic, dynamic> DeleteCustomersCustomerCardsId;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerCardsId;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerCardsId;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerCashBalance;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerCashBalance;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerCashBalanceTransactions;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerCashBalanceTransactionsTransaction;\n");
    sb.append("  method<dynamic, dynamic> DeleteCustomersCustomerDiscount;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerDiscount;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerFundingInstructions;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerPaymentMethods;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerPaymentMethodsPaymentMethod;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerSources;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerSources;\n");
    sb.append("  method<dynamic, dynamic> DeleteCustomersCustomerSourcesId;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerSourcesId;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerSourcesId;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerSourcesIdVerify;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerSubscriptions;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerSubscriptions;\n");
    sb.append("  method<dynamic, dynamic> DeleteCustomersCustomerSubscriptionsSubscriptionExposedId;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerSubscriptionsSubscriptionExposedId;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerSubscriptionsSubscriptionExposedId;\n");
    sb.append("  method<dynamic, dynamic> DeleteCustomersCustomerSubscriptionsSubscriptionExposedIdDiscount;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerSubscriptionsSubscriptionExposedIdDiscount;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerTaxIds;\n");
    sb.append("  method<dynamic, dynamic> PostCustomersCustomerTaxIds;\n");
    sb.append("  method<dynamic, dynamic> DeleteCustomersCustomerTaxIdsId;\n");
    sb.append("  method<dynamic, dynamic> GetCustomersCustomerTaxIdsId;\n");
    sb.append("  method<dynamic, dynamic> GetDisputes;\n");
    sb.append("  method<dynamic, dynamic> GetDisputesDispute;\n");
    sb.append("  method<dynamic, dynamic> PostDisputesDispute;\n");
    sb.append("  method<dynamic, dynamic> PostDisputesDisputeClose;\n");
    sb.append("  method<dynamic, dynamic> PostEphemeralKeys;\n");
    sb.append("  method<dynamic, dynamic> DeleteEphemeralKeysKey;\n");
    sb.append("  method<dynamic, dynamic> GetEvents;\n");
    sb.append("  method<dynamic, dynamic> GetEventsId;\n");
    sb.append("  method<dynamic, dynamic> GetExchangeRates;\n");
    sb.append("  method<dynamic, dynamic> GetExchangeRatesRateId;\n");
    sb.append("  method<dynamic, dynamic> GetFileLinks;\n");
    sb.append("  method<dynamic, dynamic> PostFileLinks;\n");
    sb.append("  method<dynamic, dynamic> GetFileLinksLink;\n");
    sb.append("  method<dynamic, dynamic> PostFileLinksLink;\n");
    sb.append("  method<dynamic, dynamic> GetFiles;\n");
    sb.append("  method<dynamic, dynamic> PostFiles;\n");
    sb.append("  method<dynamic, dynamic> GetFilesFile;\n");
    sb.append("  method<dynamic, dynamic> GetFinancialConnectionsAccounts;\n");
    sb.append("  method<dynamic, dynamic> GetFinancialConnectionsAccountsAccount;\n");
    sb.append("  method<dynamic, dynamic> PostFinancialConnectionsAccountsAccountDisconnect;\n");
    sb.append("  method<dynamic, dynamic> GetFinancialConnectionsAccountsAccountOwners;\n");
    sb.append("  method<dynamic, dynamic> PostFinancialConnectionsAccountsAccountRefresh;\n");
    sb.append("  method<dynamic, dynamic> PostFinancialConnectionsSessions;\n");
    sb.append("  method<dynamic, dynamic> GetFinancialConnectionsSessionsSession;\n");
    sb.append("  method<dynamic, dynamic> GetIdentityVerificationReports;\n");
    sb.append("  method<dynamic, dynamic> GetIdentityVerificationReportsReport;\n");
    sb.append("  method<dynamic, dynamic> GetIdentityVerificationSessions;\n");
    sb.append("  method<dynamic, dynamic> PostIdentityVerificationSessions;\n");
    sb.append("  method<dynamic, dynamic> GetIdentityVerificationSessionsSession;\n");
    sb.append("  method<dynamic, dynamic> PostIdentityVerificationSessionsSession;\n");
    sb.append("  method<dynamic, dynamic> PostIdentityVerificationSessionsSessionCancel;\n");
    sb.append("  method<dynamic, dynamic> PostIdentityVerificationSessionsSessionRedact;\n");
    sb.append("  method<dynamic, dynamic> GetInvoiceitems;\n");
    sb.append("  method<dynamic, dynamic> PostInvoiceitems;\n");
    sb.append("  method<dynamic, dynamic> DeleteInvoiceitemsInvoiceitem;\n");
    sb.append("  method<dynamic, dynamic> GetInvoiceitemsInvoiceitem;\n");
    sb.append("  method<dynamic, dynamic> PostInvoiceitemsInvoiceitem;\n");
    sb.append("  method<dynamic, dynamic> GetInvoices;\n");
    sb.append("  method<dynamic, dynamic> PostInvoices;\n");
    sb.append("  method<dynamic, dynamic> GetInvoicesSearch;\n");
    sb.append("  method<dynamic, dynamic> GetInvoicesUpcoming;\n");
    sb.append("  method<dynamic, dynamic> GetInvoicesUpcomingLines;\n");
    sb.append("  method<dynamic, dynamic> DeleteInvoicesInvoice;\n");
    sb.append("  method<dynamic, dynamic> GetInvoicesInvoice;\n");
    sb.append("  method<dynamic, dynamic> PostInvoicesInvoice;\n");
    sb.append("  method<dynamic, dynamic> PostInvoicesInvoiceFinalize;\n");
    sb.append("  method<dynamic, dynamic> GetInvoicesInvoiceLines;\n");
    sb.append("  method<dynamic, dynamic> PostInvoicesInvoiceMarkUncollectible;\n");
    sb.append("  method<dynamic, dynamic> PostInvoicesInvoicePay;\n");
    sb.append("  method<dynamic, dynamic> PostInvoicesInvoiceSend;\n");
    sb.append("  method<dynamic, dynamic> PostInvoicesInvoiceVoid;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingAuthorizations;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingAuthorizationsAuthorization;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingAuthorizationsAuthorization;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingAuthorizationsAuthorizationApprove;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingAuthorizationsAuthorizationDecline;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingCardholders;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingCardholders;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingCardholdersCardholder;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingCardholdersCardholder;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingCards;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingCards;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingCardsCard;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingCardsCard;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingDisputes;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingDisputes;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingDisputesDispute;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingDisputesDispute;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingDisputesDisputeSubmit;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingSettlements;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingSettlementsSettlement;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingSettlementsSettlement;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingTransactions;\n");
    sb.append("  method<dynamic, dynamic> GetIssuingTransactionsTransaction;\n");
    sb.append("  method<dynamic, dynamic> PostIssuingTransactionsTransaction;\n");
    sb.append("  method<dynamic, dynamic> PostLinkAccountSessions;\n");
    sb.append("  method<dynamic, dynamic> GetLinkAccountSessionsSession;\n");
    sb.append("  method<dynamic, dynamic> GetLinkedAccounts;\n");
    sb.append("  method<dynamic, dynamic> GetLinkedAccountsAccount;\n");
    sb.append("  method<dynamic, dynamic> PostLinkedAccountsAccountDisconnect;\n");
    sb.append("  method<dynamic, dynamic> GetLinkedAccountsAccountOwners;\n");
    sb.append("  method<dynamic, dynamic> PostLinkedAccountsAccountRefresh;\n");
    sb.append("  method<dynamic, dynamic> GetMandatesMandate;\n");
    sb.append("  method<dynamic, dynamic> GetPaymentIntents;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentIntents;\n");
    sb.append("  method<dynamic, dynamic> GetPaymentIntentsSearch;\n");
    sb.append("  method<dynamic, dynamic> GetPaymentIntentsIntent;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentIntentsIntent;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentIntentsIntentApplyCustomerBalance;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentIntentsIntentCancel;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentIntentsIntentCapture;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentIntentsIntentConfirm;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentIntentsIntentIncrementAuthorization;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentIntentsIntentVerifyMicrodeposits;\n");
    sb.append("  method<dynamic, dynamic> GetPaymentLinks;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentLinks;\n");
    sb.append("  method<dynamic, dynamic> GetPaymentLinksPaymentLink;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentLinksPaymentLink;\n");
    sb.append("  method<dynamic, dynamic> GetPaymentLinksPaymentLinkLineItems;\n");
    sb.append("  method<dynamic, dynamic> GetPaymentMethods;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentMethods;\n");
    sb.append("  method<dynamic, dynamic> GetPaymentMethodsPaymentMethod;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentMethodsPaymentMethod;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentMethodsPaymentMethodAttach;\n");
    sb.append("  method<dynamic, dynamic> PostPaymentMethodsPaymentMethodDetach;\n");
    sb.append("  method<dynamic, dynamic> GetPayouts;\n");
    sb.append("  method<dynamic, dynamic> PostPayouts;\n");
    sb.append("  method<dynamic, dynamic> GetPayoutsPayout;\n");
    sb.append("  method<dynamic, dynamic> PostPayoutsPayout;\n");
    sb.append("  method<dynamic, dynamic> PostPayoutsPayoutCancel;\n");
    sb.append("  method<dynamic, dynamic> PostPayoutsPayoutReverse;\n");
    sb.append("  method<dynamic, dynamic> GetPlans;\n");
    sb.append("  method<dynamic, dynamic> PostPlans;\n");
    sb.append("  method<dynamic, dynamic> DeletePlansPlan;\n");
    sb.append("  method<dynamic, dynamic> GetPlansPlan;\n");
    sb.append("  method<dynamic, dynamic> PostPlansPlan;\n");
    sb.append("  method<dynamic, dynamic> GetPrices;\n");
    sb.append("  method<dynamic, dynamic> PostPrices;\n");
    sb.append("  method<dynamic, dynamic> GetPricesSearch;\n");
    sb.append("  method<dynamic, dynamic> GetPricesPrice;\n");
    sb.append("  method<dynamic, dynamic> PostPricesPrice;\n");
    sb.append("  method<dynamic, dynamic> GetProducts;\n");
    sb.append("  method<dynamic, dynamic> PostProducts;\n");
    sb.append("  method<dynamic, dynamic> GetProductsSearch;\n");
    sb.append("  method<dynamic, dynamic> DeleteProductsId;\n");
    sb.append("  method<dynamic, dynamic> GetProductsId;\n");
    sb.append("  method<dynamic, dynamic> PostProductsId;\n");
    sb.append("  method<dynamic, dynamic> GetPromotionCodes;\n");
    sb.append("  method<dynamic, dynamic> PostPromotionCodes;\n");
    sb.append("  method<dynamic, dynamic> GetPromotionCodesPromotionCode;\n");
    sb.append("  method<dynamic, dynamic> PostPromotionCodesPromotionCode;\n");
    sb.append("  method<dynamic, dynamic> GetQuotes;\n");
    sb.append("  method<dynamic, dynamic> PostQuotes;\n");
    sb.append("  method<dynamic, dynamic> GetQuotesQuote;\n");
    sb.append("  method<dynamic, dynamic> PostQuotesQuote;\n");
    sb.append("  method<dynamic, dynamic> PostQuotesQuoteAccept;\n");
    sb.append("  method<dynamic, dynamic> PostQuotesQuoteCancel;\n");
    sb.append("  method<dynamic, dynamic> GetQuotesQuoteComputedUpfrontLineItems;\n");
    sb.append("  method<dynamic, dynamic> PostQuotesQuoteFinalize;\n");
    sb.append("  method<dynamic, dynamic> GetQuotesQuoteLineItems;\n");
    sb.append("  method<dynamic, dynamic> GetQuotesQuotePdf;\n");
    sb.append("  method<dynamic, dynamic> GetRadarEarlyFraudWarnings;\n");
    sb.append("  method<dynamic, dynamic> GetRadarEarlyFraudWarningsEarlyFraudWarning;\n");
    sb.append("  method<dynamic, dynamic> GetRadarValueListItems;\n");
    sb.append("  method<dynamic, dynamic> PostRadarValueListItems;\n");
    sb.append("  method<dynamic, dynamic> DeleteRadarValueListItemsItem;\n");
    sb.append("  method<dynamic, dynamic> GetRadarValueListItemsItem;\n");
    sb.append("  method<dynamic, dynamic> GetRadarValueLists;\n");
    sb.append("  method<dynamic, dynamic> PostRadarValueLists;\n");
    sb.append("  method<dynamic, dynamic> DeleteRadarValueListsValueList;\n");
    sb.append("  method<dynamic, dynamic> GetRadarValueListsValueList;\n");
    sb.append("  method<dynamic, dynamic> PostRadarValueListsValueList;\n");
    sb.append("  method<dynamic, dynamic> GetRefunds;\n");
    sb.append("  method<dynamic, dynamic> PostRefunds;\n");
    sb.append("  method<dynamic, dynamic> GetRefundsRefund;\n");
    sb.append("  method<dynamic, dynamic> PostRefundsRefund;\n");
    sb.append("  method<dynamic, dynamic> PostRefundsRefundCancel;\n");
    sb.append("  method<dynamic, dynamic> GetReportingReportRuns;\n");
    sb.append("  method<dynamic, dynamic> PostReportingReportRuns;\n");
    sb.append("  method<dynamic, dynamic> GetReportingReportRunsReportRun;\n");
    sb.append("  method<dynamic, dynamic> GetReportingReportTypes;\n");
    sb.append("  method<dynamic, dynamic> GetReportingReportTypesReportType;\n");
    sb.append("  method<dynamic, dynamic> GetReviews;\n");
    sb.append("  method<dynamic, dynamic> GetReviewsReview;\n");
    sb.append("  method<dynamic, dynamic> PostReviewsReviewApprove;\n");
    sb.append("  method<dynamic, dynamic> GetSetupAttempts;\n");
    sb.append("  method<dynamic, dynamic> GetSetupIntents;\n");
    sb.append("  method<dynamic, dynamic> PostSetupIntents;\n");
    sb.append("  method<dynamic, dynamic> GetSetupIntentsIntent;\n");
    sb.append("  method<dynamic, dynamic> PostSetupIntentsIntent;\n");
    sb.append("  method<dynamic, dynamic> PostSetupIntentsIntentCancel;\n");
    sb.append("  method<dynamic, dynamic> PostSetupIntentsIntentConfirm;\n");
    sb.append("  method<dynamic, dynamic> PostSetupIntentsIntentVerifyMicrodeposits;\n");
    sb.append("  method<dynamic, dynamic> GetShippingRates;\n");
    sb.append("  method<dynamic, dynamic> PostShippingRates;\n");
    sb.append("  method<dynamic, dynamic> GetShippingRatesShippingRateToken;\n");
    sb.append("  method<dynamic, dynamic> PostShippingRatesShippingRateToken;\n");
    sb.append("  method<dynamic, dynamic> GetSigmaScheduledQueryRuns;\n");
    sb.append("  method<dynamic, dynamic> GetSigmaScheduledQueryRunsScheduledQueryRun;\n");
    sb.append("  method<dynamic, dynamic> PostSources;\n");
    sb.append("  method<dynamic, dynamic> GetSourcesSource;\n");
    sb.append("  method<dynamic, dynamic> PostSourcesSource;\n");
    sb.append("  method<dynamic, dynamic> GetSourcesSourceMandateNotificationsMandateNotification;\n");
    sb.append("  method<dynamic, dynamic> GetSourcesSourceSourceTransactions;\n");
    sb.append("  method<dynamic, dynamic> GetSourcesSourceSourceTransactionsSourceTransaction;\n");
    sb.append("  method<dynamic, dynamic> PostSourcesSourceVerify;\n");
    sb.append("  method<dynamic, dynamic> GetSubscriptionItems;\n");
    sb.append("  method<dynamic, dynamic> PostSubscriptionItems;\n");
    sb.append("  method<dynamic, dynamic> DeleteSubscriptionItemsItem;\n");
    sb.append("  method<dynamic, dynamic> GetSubscriptionItemsItem;\n");
    sb.append("  method<dynamic, dynamic> PostSubscriptionItemsItem;\n");
    sb.append("  method<dynamic, dynamic> GetSubscriptionItemsSubscriptionItemUsageRecordSummaries;\n");
    sb.append("  method<dynamic, dynamic> PostSubscriptionItemsSubscriptionItemUsageRecords;\n");
    sb.append("  method<dynamic, dynamic> GetSubscriptionSchedules;\n");
    sb.append("  method<dynamic, dynamic> PostSubscriptionSchedules;\n");
    sb.append("  method<dynamic, dynamic> GetSubscriptionSchedulesSchedule;\n");
    sb.append("  method<dynamic, dynamic> PostSubscriptionSchedulesSchedule;\n");
    sb.append("  method<dynamic, dynamic> PostSubscriptionSchedulesScheduleCancel;\n");
    sb.append("  method<dynamic, dynamic> PostSubscriptionSchedulesScheduleRelease;\n");
    sb.append("  method<dynamic, dynamic> GetSubscriptions;\n");
    sb.append("  method<dynamic, dynamic> PostSubscriptions;\n");
    sb.append("  method<dynamic, dynamic> GetSubscriptionsSearch;\n");
    sb.append("  method<dynamic, dynamic> DeleteSubscriptionsSubscriptionExposedId;\n");
    sb.append("  method<dynamic, dynamic> GetSubscriptionsSubscriptionExposedId;\n");
    sb.append("  method<dynamic, dynamic> PostSubscriptionsSubscriptionExposedId;\n");
    sb.append("  method<dynamic, dynamic> DeleteSubscriptionsSubscriptionExposedIdDiscount;\n");
    sb.append("  method<dynamic, dynamic> PostSubscriptionsSubscriptionResume;\n");
    sb.append("  method<dynamic, dynamic> PostTaxCalculations;\n");
    sb.append("  method<dynamic, dynamic> GetTaxCalculationsCalculationLineItems;\n");
    sb.append("  method<dynamic, dynamic> PostTaxTransactionsCreateFromCalculation;\n");
    sb.append("  method<dynamic, dynamic> PostTaxTransactionsCreateReversal;\n");
    sb.append("  method<dynamic, dynamic> GetTaxTransactionsTransaction;\n");
    sb.append("  method<dynamic, dynamic> GetTaxTransactionsTransactionLineItems;\n");
    sb.append("  method<dynamic, dynamic> GetTaxCodes;\n");
    sb.append("  method<dynamic, dynamic> GetTaxCodesId;\n");
    sb.append("  method<dynamic, dynamic> GetTaxRates;\n");
    sb.append("  method<dynamic, dynamic> PostTaxRates;\n");
    sb.append("  method<dynamic, dynamic> GetTaxRatesTaxRate;\n");
    sb.append("  method<dynamic, dynamic> PostTaxRatesTaxRate;\n");
    sb.append("  method<dynamic, dynamic> GetTerminalConfigurations;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalConfigurations;\n");
    sb.append("  method<dynamic, dynamic> DeleteTerminalConfigurationsConfiguration;\n");
    sb.append("  method<dynamic, dynamic> GetTerminalConfigurationsConfiguration;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalConfigurationsConfiguration;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalConnectionTokens;\n");
    sb.append("  method<dynamic, dynamic> GetTerminalLocations;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalLocations;\n");
    sb.append("  method<dynamic, dynamic> DeleteTerminalLocationsLocation;\n");
    sb.append("  method<dynamic, dynamic> GetTerminalLocationsLocation;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalLocationsLocation;\n");
    sb.append("  method<dynamic, dynamic> GetTerminalReaders;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalReaders;\n");
    sb.append("  method<dynamic, dynamic> DeleteTerminalReadersReader;\n");
    sb.append("  method<dynamic, dynamic> GetTerminalReadersReader;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalReadersReader;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalReadersReaderCancelAction;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalReadersReaderProcessPaymentIntent;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalReadersReaderProcessSetupIntent;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalReadersReaderRefundPayment;\n");
    sb.append("  method<dynamic, dynamic> PostTerminalReadersReaderSetReaderDisplay;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersCustomersCustomerFundCashBalance;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersIssuingCardsCardShippingDeliver;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersIssuingCardsCardShippingFail;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersIssuingCardsCardShippingReturn;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersIssuingCardsCardShippingShip;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersRefundsRefundExpire;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTerminalReadersReaderPresentPaymentMethod;\n");
    sb.append("  method<dynamic, dynamic> GetTestHelpersTestClocks;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTestClocks;\n");
    sb.append("  method<dynamic, dynamic> DeleteTestHelpersTestClocksTestClock;\n");
    sb.append("  method<dynamic, dynamic> GetTestHelpersTestClocksTestClock;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTestClocksTestClockAdvance;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTreasuryInboundTransfersIdFail;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTreasuryInboundTransfersIdReturn;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTreasuryInboundTransfersIdSucceed;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTreasuryOutboundPaymentsIdFail;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTreasuryOutboundPaymentsIdPost;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTreasuryOutboundPaymentsIdReturn;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTreasuryOutboundTransfersOutboundTransferFail;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTreasuryOutboundTransfersOutboundTransferPost;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTreasuryOutboundTransfersOutboundTransferReturn;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTreasuryReceivedCredits;\n");
    sb.append("  method<dynamic, dynamic> PostTestHelpersTreasuryReceivedDebits;\n");
    sb.append("  method<dynamic, dynamic> PostTokens;\n");
    sb.append("  method<dynamic, dynamic> GetTokensToken;\n");
    sb.append("  method<dynamic, dynamic> GetTopups;\n");
    sb.append("  method<dynamic, dynamic> PostTopups;\n");
    sb.append("  method<dynamic, dynamic> GetTopupsTopup;\n");
    sb.append("  method<dynamic, dynamic> PostTopupsTopup;\n");
    sb.append("  method<dynamic, dynamic> PostTopupsTopupCancel;\n");
    sb.append("  method<dynamic, dynamic> GetTransfers;\n");
    sb.append("  method<dynamic, dynamic> PostTransfers;\n");
    sb.append("  method<dynamic, dynamic> GetTransfersIdReversals;\n");
    sb.append("  method<dynamic, dynamic> PostTransfersIdReversals;\n");
    sb.append("  method<dynamic, dynamic> GetTransfersTransfer;\n");
    sb.append("  method<dynamic, dynamic> PostTransfersTransfer;\n");
    sb.append("  method<dynamic, dynamic> GetTransfersTransferReversalsId;\n");
    sb.append("  method<dynamic, dynamic> PostTransfersTransferReversalsId;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryCreditReversals;\n");
    sb.append("  method<dynamic, dynamic> PostTreasuryCreditReversals;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryCreditReversalsCreditReversal;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryDebitReversals;\n");
    sb.append("  method<dynamic, dynamic> PostTreasuryDebitReversals;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryDebitReversalsDebitReversal;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryFinancialAccounts;\n");
    sb.append("  method<dynamic, dynamic> PostTreasuryFinancialAccounts;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryFinancialAccountsFinancialAccount;\n");
    sb.append("  method<dynamic, dynamic> PostTreasuryFinancialAccountsFinancialAccount;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryFinancialAccountsFinancialAccountFeatures;\n");
    sb.append("  method<dynamic, dynamic> PostTreasuryFinancialAccountsFinancialAccountFeatures;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryInboundTransfers;\n");
    sb.append("  method<dynamic, dynamic> PostTreasuryInboundTransfers;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryInboundTransfersId;\n");
    sb.append("  method<dynamic, dynamic> PostTreasuryInboundTransfersInboundTransferCancel;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryOutboundPayments;\n");
    sb.append("  method<dynamic, dynamic> PostTreasuryOutboundPayments;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryOutboundPaymentsId;\n");
    sb.append("  method<dynamic, dynamic> PostTreasuryOutboundPaymentsIdCancel;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryOutboundTransfers;\n");
    sb.append("  method<dynamic, dynamic> PostTreasuryOutboundTransfers;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryOutboundTransfersOutboundTransfer;\n");
    sb.append("  method<dynamic, dynamic> PostTreasuryOutboundTransfersOutboundTransferCancel;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryReceivedCredits;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryReceivedCreditsId;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryReceivedDebits;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryReceivedDebitsId;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryTransactionEntries;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryTransactionEntriesId;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryTransactions;\n");
    sb.append("  method<dynamic, dynamic> GetTreasuryTransactionsId;\n");
    sb.append("  method<dynamic, dynamic> GetWebhookEndpoints;\n");
    sb.append("  method<dynamic, dynamic> PostWebhookEndpoints;\n");
    sb.append("  method<dynamic, dynamic> DeleteWebhookEndpointsWebhookEndpoint;\n");
    sb.append("  method<dynamic, dynamic> GetWebhookEndpointsWebhookEndpoint;\n");
    sb.append("  method<dynamic, dynamic> PostWebhookEndpointsWebhookEndpoint;\n");
/** END[CODEGEN-DEFN] **/
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    ObjectNode node = Json.parseJsonObject(request);
    switch (method) {
/** BEGIN[CODEGEN-METHODS] **/
      case "GetAccount":
        invoke("GET", "/v1/account", node, callback);
        return;
      case "PostAccountLinks":
        invoke("POST", "/v1/account_links", node, callback);
        return;
      case "GetAccounts":
        invoke("GET", "/v1/accounts", node, callback);
        return;
      case "PostAccounts":
        invoke("POST", "/v1/accounts", node, callback);
        return;
      case "DeleteAccountsAccount":
        invoke("DELETE", "/v1/accounts/" + Json.readStringAndRemove(node, "account"), node, callback);
        return;
      case "GetAccountsAccount":
        invoke("GET", "/v1/accounts/" + Json.readStringAndRemove(node, "account"), node, callback);
        return;
      case "PostAccountsAccount":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account"), node, callback);
        return;
      case "PostAccountsAccountBankAccounts":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/bank_accounts", node, callback);
        return;
      case "DeleteAccountsAccountBankAccountsId":
        invoke("DELETE", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/bank_accounts/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetAccountsAccountBankAccountsId":
        invoke("GET", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/bank_accounts/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostAccountsAccountBankAccountsId":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/bank_accounts/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetAccountsAccountCapabilities":
        invoke("GET", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/capabilities", node, callback);
        return;
      case "GetAccountsAccountCapabilitiesCapability":
        invoke("GET", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/capabilities/" + Json.readStringAndRemove(node, "capability"), node, callback);
        return;
      case "PostAccountsAccountCapabilitiesCapability":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/capabilities/" + Json.readStringAndRemove(node, "capability"), node, callback);
        return;
      case "GetAccountsAccountExternalAccounts":
        invoke("GET", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/external_accounts", node, callback);
        return;
      case "PostAccountsAccountExternalAccounts":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/external_accounts", node, callback);
        return;
      case "DeleteAccountsAccountExternalAccountsId":
        invoke("DELETE", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/external_accounts/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetAccountsAccountExternalAccountsId":
        invoke("GET", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/external_accounts/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostAccountsAccountExternalAccountsId":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/external_accounts/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostAccountsAccountLoginLinks":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/login_links", node, callback);
        return;
      case "GetAccountsAccountPeople":
        invoke("GET", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/people", node, callback);
        return;
      case "PostAccountsAccountPeople":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/people", node, callback);
        return;
      case "DeleteAccountsAccountPeoplePerson":
        invoke("DELETE", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/people/" + Json.readStringAndRemove(node, "person"), node, callback);
        return;
      case "GetAccountsAccountPeoplePerson":
        invoke("GET", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/people/" + Json.readStringAndRemove(node, "person"), node, callback);
        return;
      case "PostAccountsAccountPeoplePerson":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/people/" + Json.readStringAndRemove(node, "person"), node, callback);
        return;
      case "GetAccountsAccountPersons":
        invoke("GET", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/persons", node, callback);
        return;
      case "PostAccountsAccountPersons":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/persons", node, callback);
        return;
      case "DeleteAccountsAccountPersonsPerson":
        invoke("DELETE", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/persons/" + Json.readStringAndRemove(node, "person"), node, callback);
        return;
      case "GetAccountsAccountPersonsPerson":
        invoke("GET", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/persons/" + Json.readStringAndRemove(node, "person"), node, callback);
        return;
      case "PostAccountsAccountPersonsPerson":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/persons/" + Json.readStringAndRemove(node, "person"), node, callback);
        return;
      case "PostAccountsAccountReject":
        invoke("POST", "/v1/accounts/" + Json.readStringAndRemove(node, "account") + "/reject", node, callback);
        return;
      case "GetApplePayDomains":
        invoke("GET", "/v1/apple_pay/domains", node, callback);
        return;
      case "PostApplePayDomains":
        invoke("POST", "/v1/apple_pay/domains", node, callback);
        return;
      case "DeleteApplePayDomainsDomain":
        invoke("DELETE", "/v1/apple_pay/domains/" + Json.readStringAndRemove(node, "domain"), node, callback);
        return;
      case "GetApplePayDomainsDomain":
        invoke("GET", "/v1/apple_pay/domains/" + Json.readStringAndRemove(node, "domain"), node, callback);
        return;
      case "GetApplicationFees":
        invoke("GET", "/v1/application_fees", node, callback);
        return;
      case "GetApplicationFeesFeeRefundsId":
        invoke("GET", "/v1/application_fees/" + Json.readStringAndRemove(node, "fee") + "/refunds/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostApplicationFeesFeeRefundsId":
        invoke("POST", "/v1/application_fees/" + Json.readStringAndRemove(node, "fee") + "/refunds/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetApplicationFeesId":
        invoke("GET", "/v1/application_fees/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostApplicationFeesIdRefund":
        invoke("POST", "/v1/application_fees/" + Json.readStringAndRemove(node, "id") + "/refund", node, callback);
        return;
      case "GetApplicationFeesIdRefunds":
        invoke("GET", "/v1/application_fees/" + Json.readStringAndRemove(node, "id") + "/refunds", node, callback);
        return;
      case "PostApplicationFeesIdRefunds":
        invoke("POST", "/v1/application_fees/" + Json.readStringAndRemove(node, "id") + "/refunds", node, callback);
        return;
      case "GetAppsSecrets":
        invoke("GET", "/v1/apps/secrets", node, callback);
        return;
      case "PostAppsSecrets":
        invoke("POST", "/v1/apps/secrets", node, callback);
        return;
      case "PostAppsSecretsDelete":
        invoke("POST", "/v1/apps/secrets/delete", node, callback);
        return;
      case "GetAppsSecretsFind":
        invoke("GET", "/v1/apps/secrets/find", node, callback);
        return;
      case "GetBalance":
        invoke("GET", "/v1/balance", node, callback);
        return;
      case "GetBalanceHistory":
        invoke("GET", "/v1/balance/history", node, callback);
        return;
      case "GetBalanceHistoryId":
        invoke("GET", "/v1/balance/history/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetBalanceTransactions":
        invoke("GET", "/v1/balance_transactions", node, callback);
        return;
      case "GetBalanceTransactionsId":
        invoke("GET", "/v1/balance_transactions/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetBillingPortalConfigurations":
        invoke("GET", "/v1/billing_portal/configurations", node, callback);
        return;
      case "PostBillingPortalConfigurations":
        invoke("POST", "/v1/billing_portal/configurations", node, callback);
        return;
      case "GetBillingPortalConfigurationsConfiguration":
        invoke("GET", "/v1/billing_portal/configurations/" + Json.readStringAndRemove(node, "configuration"), node, callback);
        return;
      case "PostBillingPortalConfigurationsConfiguration":
        invoke("POST", "/v1/billing_portal/configurations/" + Json.readStringAndRemove(node, "configuration"), node, callback);
        return;
      case "PostBillingPortalSessions":
        invoke("POST", "/v1/billing_portal/sessions", node, callback);
        return;
      case "GetCharges":
        invoke("GET", "/v1/charges", node, callback);
        return;
      case "PostCharges":
        invoke("POST", "/v1/charges", node, callback);
        return;
      case "GetChargesSearch":
        invoke("GET", "/v1/charges/search", node, callback);
        return;
      case "GetChargesCharge":
        invoke("GET", "/v1/charges/" + Json.readStringAndRemove(node, "charge"), node, callback);
        return;
      case "PostChargesCharge":
        invoke("POST", "/v1/charges/" + Json.readStringAndRemove(node, "charge"), node, callback);
        return;
      case "PostChargesChargeCapture":
        invoke("POST", "/v1/charges/" + Json.readStringAndRemove(node, "charge") + "/capture", node, callback);
        return;
      case "GetChargesChargeDispute":
        invoke("GET", "/v1/charges/" + Json.readStringAndRemove(node, "charge") + "/dispute", node, callback);
        return;
      case "PostChargesChargeDispute":
        invoke("POST", "/v1/charges/" + Json.readStringAndRemove(node, "charge") + "/dispute", node, callback);
        return;
      case "PostChargesChargeDisputeClose":
        invoke("POST", "/v1/charges/" + Json.readStringAndRemove(node, "charge") + "/dispute/close", node, callback);
        return;
      case "PostChargesChargeRefund":
        invoke("POST", "/v1/charges/" + Json.readStringAndRemove(node, "charge") + "/refund", node, callback);
        return;
      case "GetChargesChargeRefunds":
        invoke("GET", "/v1/charges/" + Json.readStringAndRemove(node, "charge") + "/refunds", node, callback);
        return;
      case "PostChargesChargeRefunds":
        invoke("POST", "/v1/charges/" + Json.readStringAndRemove(node, "charge") + "/refunds", node, callback);
        return;
      case "GetChargesChargeRefundsRefund":
        invoke("GET", "/v1/charges/" + Json.readStringAndRemove(node, "charge") + "/refunds/" + Json.readStringAndRemove(node, "refund"), node, callback);
        return;
      case "PostChargesChargeRefundsRefund":
        invoke("POST", "/v1/charges/" + Json.readStringAndRemove(node, "charge") + "/refunds/" + Json.readStringAndRemove(node, "refund"), node, callback);
        return;
      case "GetCheckoutSessions":
        invoke("GET", "/v1/checkout/sessions", node, callback);
        return;
      case "PostCheckoutSessions":
        invoke("POST", "/v1/checkout/sessions", node, callback);
        return;
      case "GetCheckoutSessionsSession":
        invoke("GET", "/v1/checkout/sessions/" + Json.readStringAndRemove(node, "session"), node, callback);
        return;
      case "PostCheckoutSessionsSessionExpire":
        invoke("POST", "/v1/checkout/sessions/" + Json.readStringAndRemove(node, "session") + "/expire", node, callback);
        return;
      case "GetCheckoutSessionsSessionLineItems":
        invoke("GET", "/v1/checkout/sessions/" + Json.readStringAndRemove(node, "session") + "/line_items", node, callback);
        return;
      case "GetCountrySpecs":
        invoke("GET", "/v1/country_specs", node, callback);
        return;
      case "GetCountrySpecsCountry":
        invoke("GET", "/v1/country_specs/" + Json.readStringAndRemove(node, "country"), node, callback);
        return;
      case "GetCoupons":
        invoke("GET", "/v1/coupons", node, callback);
        return;
      case "PostCoupons":
        invoke("POST", "/v1/coupons", node, callback);
        return;
      case "DeleteCouponsCoupon":
        invoke("DELETE", "/v1/coupons/" + Json.readStringAndRemove(node, "coupon"), node, callback);
        return;
      case "GetCouponsCoupon":
        invoke("GET", "/v1/coupons/" + Json.readStringAndRemove(node, "coupon"), node, callback);
        return;
      case "PostCouponsCoupon":
        invoke("POST", "/v1/coupons/" + Json.readStringAndRemove(node, "coupon"), node, callback);
        return;
      case "GetCreditNotes":
        invoke("GET", "/v1/credit_notes", node, callback);
        return;
      case "PostCreditNotes":
        invoke("POST", "/v1/credit_notes", node, callback);
        return;
      case "GetCreditNotesPreview":
        invoke("GET", "/v1/credit_notes/preview", node, callback);
        return;
      case "GetCreditNotesPreviewLines":
        invoke("GET", "/v1/credit_notes/preview/lines", node, callback);
        return;
      case "GetCreditNotesCreditNoteLines":
        invoke("GET", "/v1/credit_notes/" + Json.readStringAndRemove(node, "credit_note") + "/lines", node, callback);
        return;
      case "GetCreditNotesId":
        invoke("GET", "/v1/credit_notes/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostCreditNotesId":
        invoke("POST", "/v1/credit_notes/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostCreditNotesIdVoid":
        invoke("POST", "/v1/credit_notes/" + Json.readStringAndRemove(node, "id") + "/void", node, callback);
        return;
      case "GetCustomers":
        invoke("GET", "/v1/customers", node, callback);
        return;
      case "PostCustomers":
        invoke("POST", "/v1/customers", node, callback);
        return;
      case "GetCustomersSearch":
        invoke("GET", "/v1/customers/search", node, callback);
        return;
      case "DeleteCustomersCustomer":
        invoke("DELETE", "/v1/customers/" + Json.readStringAndRemove(node, "customer"), node, callback);
        return;
      case "GetCustomersCustomer":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer"), node, callback);
        return;
      case "PostCustomersCustomer":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer"), node, callback);
        return;
      case "GetCustomersCustomerBalanceTransactions":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/balance_transactions", node, callback);
        return;
      case "PostCustomersCustomerBalanceTransactions":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/balance_transactions", node, callback);
        return;
      case "GetCustomersCustomerBalanceTransactionsTransaction":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/balance_transactions/" + Json.readStringAndRemove(node, "transaction"), node, callback);
        return;
      case "PostCustomersCustomerBalanceTransactionsTransaction":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/balance_transactions/" + Json.readStringAndRemove(node, "transaction"), node, callback);
        return;
      case "GetCustomersCustomerBankAccounts":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/bank_accounts", node, callback);
        return;
      case "PostCustomersCustomerBankAccounts":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/bank_accounts", node, callback);
        return;
      case "DeleteCustomersCustomerBankAccountsId":
        invoke("DELETE", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/bank_accounts/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetCustomersCustomerBankAccountsId":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/bank_accounts/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostCustomersCustomerBankAccountsId":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/bank_accounts/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostCustomersCustomerBankAccountsIdVerify":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/bank_accounts/" + Json.readStringAndRemove(node, "id") + "/verify", node, callback);
        return;
      case "GetCustomersCustomerCards":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/cards", node, callback);
        return;
      case "PostCustomersCustomerCards":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/cards", node, callback);
        return;
      case "DeleteCustomersCustomerCardsId":
        invoke("DELETE", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/cards/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetCustomersCustomerCardsId":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/cards/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostCustomersCustomerCardsId":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/cards/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetCustomersCustomerCashBalance":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/cash_balance", node, callback);
        return;
      case "PostCustomersCustomerCashBalance":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/cash_balance", node, callback);
        return;
      case "GetCustomersCustomerCashBalanceTransactions":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/cash_balance_transactions", node, callback);
        return;
      case "GetCustomersCustomerCashBalanceTransactionsTransaction":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/cash_balance_transactions/" + Json.readStringAndRemove(node, "transaction"), node, callback);
        return;
      case "DeleteCustomersCustomerDiscount":
        invoke("DELETE", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/discount", node, callback);
        return;
      case "GetCustomersCustomerDiscount":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/discount", node, callback);
        return;
      case "PostCustomersCustomerFundingInstructions":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/funding_instructions", node, callback);
        return;
      case "GetCustomersCustomerPaymentMethods":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/payment_methods", node, callback);
        return;
      case "GetCustomersCustomerPaymentMethodsPaymentMethod":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/payment_methods/" + Json.readStringAndRemove(node, "payment_method"), node, callback);
        return;
      case "GetCustomersCustomerSources":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/sources", node, callback);
        return;
      case "PostCustomersCustomerSources":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/sources", node, callback);
        return;
      case "DeleteCustomersCustomerSourcesId":
        invoke("DELETE", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/sources/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetCustomersCustomerSourcesId":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/sources/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostCustomersCustomerSourcesId":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/sources/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostCustomersCustomerSourcesIdVerify":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/sources/" + Json.readStringAndRemove(node, "id") + "/verify", node, callback);
        return;
      case "GetCustomersCustomerSubscriptions":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/subscriptions", node, callback);
        return;
      case "PostCustomersCustomerSubscriptions":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/subscriptions", node, callback);
        return;
      case "DeleteCustomersCustomerSubscriptionsSubscriptionExposedId":
        invoke("DELETE", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/subscriptions/" + Json.readStringAndRemove(node, "subscription_exposed_id"), node, callback);
        return;
      case "GetCustomersCustomerSubscriptionsSubscriptionExposedId":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/subscriptions/" + Json.readStringAndRemove(node, "subscription_exposed_id"), node, callback);
        return;
      case "PostCustomersCustomerSubscriptionsSubscriptionExposedId":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/subscriptions/" + Json.readStringAndRemove(node, "subscription_exposed_id"), node, callback);
        return;
      case "DeleteCustomersCustomerSubscriptionsSubscriptionExposedIdDiscount":
        invoke("DELETE", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/subscriptions/" + Json.readStringAndRemove(node, "subscription_exposed_id") + "/discount", node, callback);
        return;
      case "GetCustomersCustomerSubscriptionsSubscriptionExposedIdDiscount":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/subscriptions/" + Json.readStringAndRemove(node, "subscription_exposed_id") + "/discount", node, callback);
        return;
      case "GetCustomersCustomerTaxIds":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/tax_ids", node, callback);
        return;
      case "PostCustomersCustomerTaxIds":
        invoke("POST", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/tax_ids", node, callback);
        return;
      case "DeleteCustomersCustomerTaxIdsId":
        invoke("DELETE", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/tax_ids/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetCustomersCustomerTaxIdsId":
        invoke("GET", "/v1/customers/" + Json.readStringAndRemove(node, "customer") + "/tax_ids/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetDisputes":
        invoke("GET", "/v1/disputes", node, callback);
        return;
      case "GetDisputesDispute":
        invoke("GET", "/v1/disputes/" + Json.readStringAndRemove(node, "dispute"), node, callback);
        return;
      case "PostDisputesDispute":
        invoke("POST", "/v1/disputes/" + Json.readStringAndRemove(node, "dispute"), node, callback);
        return;
      case "PostDisputesDisputeClose":
        invoke("POST", "/v1/disputes/" + Json.readStringAndRemove(node, "dispute") + "/close", node, callback);
        return;
      case "PostEphemeralKeys":
        invoke("POST", "/v1/ephemeral_keys", node, callback);
        return;
      case "DeleteEphemeralKeysKey":
        invoke("DELETE", "/v1/ephemeral_keys/" + Json.readStringAndRemove(node, "key"), node, callback);
        return;
      case "GetEvents":
        invoke("GET", "/v1/events", node, callback);
        return;
      case "GetEventsId":
        invoke("GET", "/v1/events/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetExchangeRates":
        invoke("GET", "/v1/exchange_rates", node, callback);
        return;
      case "GetExchangeRatesRateId":
        invoke("GET", "/v1/exchange_rates/" + Json.readStringAndRemove(node, "rate_id"), node, callback);
        return;
      case "GetFileLinks":
        invoke("GET", "/v1/file_links", node, callback);
        return;
      case "PostFileLinks":
        invoke("POST", "/v1/file_links", node, callback);
        return;
      case "GetFileLinksLink":
        invoke("GET", "/v1/file_links/" + Json.readStringAndRemove(node, "link"), node, callback);
        return;
      case "PostFileLinksLink":
        invoke("POST", "/v1/file_links/" + Json.readStringAndRemove(node, "link"), node, callback);
        return;
      case "GetFiles":
        invoke("GET", "/v1/files", node, callback);
        return;
      case "PostFiles":
        invoke("POST", "/v1/files", node, callback);
        return;
      case "GetFilesFile":
        invoke("GET", "/v1/files/" + Json.readStringAndRemove(node, "file"), node, callback);
        return;
      case "GetFinancialConnectionsAccounts":
        invoke("GET", "/v1/financial_connections/accounts", node, callback);
        return;
      case "GetFinancialConnectionsAccountsAccount":
        invoke("GET", "/v1/financial_connections/accounts/" + Json.readStringAndRemove(node, "account"), node, callback);
        return;
      case "PostFinancialConnectionsAccountsAccountDisconnect":
        invoke("POST", "/v1/financial_connections/accounts/" + Json.readStringAndRemove(node, "account") + "/disconnect", node, callback);
        return;
      case "GetFinancialConnectionsAccountsAccountOwners":
        invoke("GET", "/v1/financial_connections/accounts/" + Json.readStringAndRemove(node, "account") + "/owners", node, callback);
        return;
      case "PostFinancialConnectionsAccountsAccountRefresh":
        invoke("POST", "/v1/financial_connections/accounts/" + Json.readStringAndRemove(node, "account") + "/refresh", node, callback);
        return;
      case "PostFinancialConnectionsSessions":
        invoke("POST", "/v1/financial_connections/sessions", node, callback);
        return;
      case "GetFinancialConnectionsSessionsSession":
        invoke("GET", "/v1/financial_connections/sessions/" + Json.readStringAndRemove(node, "session"), node, callback);
        return;
      case "GetIdentityVerificationReports":
        invoke("GET", "/v1/identity/verification_reports", node, callback);
        return;
      case "GetIdentityVerificationReportsReport":
        invoke("GET", "/v1/identity/verification_reports/" + Json.readStringAndRemove(node, "report"), node, callback);
        return;
      case "GetIdentityVerificationSessions":
        invoke("GET", "/v1/identity/verification_sessions", node, callback);
        return;
      case "PostIdentityVerificationSessions":
        invoke("POST", "/v1/identity/verification_sessions", node, callback);
        return;
      case "GetIdentityVerificationSessionsSession":
        invoke("GET", "/v1/identity/verification_sessions/" + Json.readStringAndRemove(node, "session"), node, callback);
        return;
      case "PostIdentityVerificationSessionsSession":
        invoke("POST", "/v1/identity/verification_sessions/" + Json.readStringAndRemove(node, "session"), node, callback);
        return;
      case "PostIdentityVerificationSessionsSessionCancel":
        invoke("POST", "/v1/identity/verification_sessions/" + Json.readStringAndRemove(node, "session") + "/cancel", node, callback);
        return;
      case "PostIdentityVerificationSessionsSessionRedact":
        invoke("POST", "/v1/identity/verification_sessions/" + Json.readStringAndRemove(node, "session") + "/redact", node, callback);
        return;
      case "GetInvoiceitems":
        invoke("GET", "/v1/invoiceitems", node, callback);
        return;
      case "PostInvoiceitems":
        invoke("POST", "/v1/invoiceitems", node, callback);
        return;
      case "DeleteInvoiceitemsInvoiceitem":
        invoke("DELETE", "/v1/invoiceitems/" + Json.readStringAndRemove(node, "invoiceitem"), node, callback);
        return;
      case "GetInvoiceitemsInvoiceitem":
        invoke("GET", "/v1/invoiceitems/" + Json.readStringAndRemove(node, "invoiceitem"), node, callback);
        return;
      case "PostInvoiceitemsInvoiceitem":
        invoke("POST", "/v1/invoiceitems/" + Json.readStringAndRemove(node, "invoiceitem"), node, callback);
        return;
      case "GetInvoices":
        invoke("GET", "/v1/invoices", node, callback);
        return;
      case "PostInvoices":
        invoke("POST", "/v1/invoices", node, callback);
        return;
      case "GetInvoicesSearch":
        invoke("GET", "/v1/invoices/search", node, callback);
        return;
      case "GetInvoicesUpcoming":
        invoke("GET", "/v1/invoices/upcoming", node, callback);
        return;
      case "GetInvoicesUpcomingLines":
        invoke("GET", "/v1/invoices/upcoming/lines", node, callback);
        return;
      case "DeleteInvoicesInvoice":
        invoke("DELETE", "/v1/invoices/" + Json.readStringAndRemove(node, "invoice"), node, callback);
        return;
      case "GetInvoicesInvoice":
        invoke("GET", "/v1/invoices/" + Json.readStringAndRemove(node, "invoice"), node, callback);
        return;
      case "PostInvoicesInvoice":
        invoke("POST", "/v1/invoices/" + Json.readStringAndRemove(node, "invoice"), node, callback);
        return;
      case "PostInvoicesInvoiceFinalize":
        invoke("POST", "/v1/invoices/" + Json.readStringAndRemove(node, "invoice") + "/finalize", node, callback);
        return;
      case "GetInvoicesInvoiceLines":
        invoke("GET", "/v1/invoices/" + Json.readStringAndRemove(node, "invoice") + "/lines", node, callback);
        return;
      case "PostInvoicesInvoiceMarkUncollectible":
        invoke("POST", "/v1/invoices/" + Json.readStringAndRemove(node, "invoice") + "/mark_uncollectible", node, callback);
        return;
      case "PostInvoicesInvoicePay":
        invoke("POST", "/v1/invoices/" + Json.readStringAndRemove(node, "invoice") + "/pay", node, callback);
        return;
      case "PostInvoicesInvoiceSend":
        invoke("POST", "/v1/invoices/" + Json.readStringAndRemove(node, "invoice") + "/send", node, callback);
        return;
      case "PostInvoicesInvoiceVoid":
        invoke("POST", "/v1/invoices/" + Json.readStringAndRemove(node, "invoice") + "/void", node, callback);
        return;
      case "GetIssuingAuthorizations":
        invoke("GET", "/v1/issuing/authorizations", node, callback);
        return;
      case "GetIssuingAuthorizationsAuthorization":
        invoke("GET", "/v1/issuing/authorizations/" + Json.readStringAndRemove(node, "authorization"), node, callback);
        return;
      case "PostIssuingAuthorizationsAuthorization":
        invoke("POST", "/v1/issuing/authorizations/" + Json.readStringAndRemove(node, "authorization"), node, callback);
        return;
      case "PostIssuingAuthorizationsAuthorizationApprove":
        invoke("POST", "/v1/issuing/authorizations/" + Json.readStringAndRemove(node, "authorization") + "/approve", node, callback);
        return;
      case "PostIssuingAuthorizationsAuthorizationDecline":
        invoke("POST", "/v1/issuing/authorizations/" + Json.readStringAndRemove(node, "authorization") + "/decline", node, callback);
        return;
      case "GetIssuingCardholders":
        invoke("GET", "/v1/issuing/cardholders", node, callback);
        return;
      case "PostIssuingCardholders":
        invoke("POST", "/v1/issuing/cardholders", node, callback);
        return;
      case "GetIssuingCardholdersCardholder":
        invoke("GET", "/v1/issuing/cardholders/" + Json.readStringAndRemove(node, "cardholder"), node, callback);
        return;
      case "PostIssuingCardholdersCardholder":
        invoke("POST", "/v1/issuing/cardholders/" + Json.readStringAndRemove(node, "cardholder"), node, callback);
        return;
      case "GetIssuingCards":
        invoke("GET", "/v1/issuing/cards", node, callback);
        return;
      case "PostIssuingCards":
        invoke("POST", "/v1/issuing/cards", node, callback);
        return;
      case "GetIssuingCardsCard":
        invoke("GET", "/v1/issuing/cards/" + Json.readStringAndRemove(node, "card"), node, callback);
        return;
      case "PostIssuingCardsCard":
        invoke("POST", "/v1/issuing/cards/" + Json.readStringAndRemove(node, "card"), node, callback);
        return;
      case "GetIssuingDisputes":
        invoke("GET", "/v1/issuing/disputes", node, callback);
        return;
      case "PostIssuingDisputes":
        invoke("POST", "/v1/issuing/disputes", node, callback);
        return;
      case "GetIssuingDisputesDispute":
        invoke("GET", "/v1/issuing/disputes/" + Json.readStringAndRemove(node, "dispute"), node, callback);
        return;
      case "PostIssuingDisputesDispute":
        invoke("POST", "/v1/issuing/disputes/" + Json.readStringAndRemove(node, "dispute"), node, callback);
        return;
      case "PostIssuingDisputesDisputeSubmit":
        invoke("POST", "/v1/issuing/disputes/" + Json.readStringAndRemove(node, "dispute") + "/submit", node, callback);
        return;
      case "GetIssuingSettlements":
        invoke("GET", "/v1/issuing/settlements", node, callback);
        return;
      case "GetIssuingSettlementsSettlement":
        invoke("GET", "/v1/issuing/settlements/" + Json.readStringAndRemove(node, "settlement"), node, callback);
        return;
      case "PostIssuingSettlementsSettlement":
        invoke("POST", "/v1/issuing/settlements/" + Json.readStringAndRemove(node, "settlement"), node, callback);
        return;
      case "GetIssuingTransactions":
        invoke("GET", "/v1/issuing/transactions", node, callback);
        return;
      case "GetIssuingTransactionsTransaction":
        invoke("GET", "/v1/issuing/transactions/" + Json.readStringAndRemove(node, "transaction"), node, callback);
        return;
      case "PostIssuingTransactionsTransaction":
        invoke("POST", "/v1/issuing/transactions/" + Json.readStringAndRemove(node, "transaction"), node, callback);
        return;
      case "PostLinkAccountSessions":
        invoke("POST", "/v1/link_account_sessions", node, callback);
        return;
      case "GetLinkAccountSessionsSession":
        invoke("GET", "/v1/link_account_sessions/" + Json.readStringAndRemove(node, "session"), node, callback);
        return;
      case "GetLinkedAccounts":
        invoke("GET", "/v1/linked_accounts", node, callback);
        return;
      case "GetLinkedAccountsAccount":
        invoke("GET", "/v1/linked_accounts/" + Json.readStringAndRemove(node, "account"), node, callback);
        return;
      case "PostLinkedAccountsAccountDisconnect":
        invoke("POST", "/v1/linked_accounts/" + Json.readStringAndRemove(node, "account") + "/disconnect", node, callback);
        return;
      case "GetLinkedAccountsAccountOwners":
        invoke("GET", "/v1/linked_accounts/" + Json.readStringAndRemove(node, "account") + "/owners", node, callback);
        return;
      case "PostLinkedAccountsAccountRefresh":
        invoke("POST", "/v1/linked_accounts/" + Json.readStringAndRemove(node, "account") + "/refresh", node, callback);
        return;
      case "GetMandatesMandate":
        invoke("GET", "/v1/mandates/" + Json.readStringAndRemove(node, "mandate"), node, callback);
        return;
      case "GetPaymentIntents":
        invoke("GET", "/v1/payment_intents", node, callback);
        return;
      case "PostPaymentIntents":
        invoke("POST", "/v1/payment_intents", node, callback);
        return;
      case "GetPaymentIntentsSearch":
        invoke("GET", "/v1/payment_intents/search", node, callback);
        return;
      case "GetPaymentIntentsIntent":
        invoke("GET", "/v1/payment_intents/" + Json.readStringAndRemove(node, "intent"), node, callback);
        return;
      case "PostPaymentIntentsIntent":
        invoke("POST", "/v1/payment_intents/" + Json.readStringAndRemove(node, "intent"), node, callback);
        return;
      case "PostPaymentIntentsIntentApplyCustomerBalance":
        invoke("POST", "/v1/payment_intents/" + Json.readStringAndRemove(node, "intent") + "/apply_customer_balance", node, callback);
        return;
      case "PostPaymentIntentsIntentCancel":
        invoke("POST", "/v1/payment_intents/" + Json.readStringAndRemove(node, "intent") + "/cancel", node, callback);
        return;
      case "PostPaymentIntentsIntentCapture":
        invoke("POST", "/v1/payment_intents/" + Json.readStringAndRemove(node, "intent") + "/capture", node, callback);
        return;
      case "PostPaymentIntentsIntentConfirm":
        invoke("POST", "/v1/payment_intents/" + Json.readStringAndRemove(node, "intent") + "/confirm", node, callback);
        return;
      case "PostPaymentIntentsIntentIncrementAuthorization":
        invoke("POST", "/v1/payment_intents/" + Json.readStringAndRemove(node, "intent") + "/increment_authorization", node, callback);
        return;
      case "PostPaymentIntentsIntentVerifyMicrodeposits":
        invoke("POST", "/v1/payment_intents/" + Json.readStringAndRemove(node, "intent") + "/verify_microdeposits", node, callback);
        return;
      case "GetPaymentLinks":
        invoke("GET", "/v1/payment_links", node, callback);
        return;
      case "PostPaymentLinks":
        invoke("POST", "/v1/payment_links", node, callback);
        return;
      case "GetPaymentLinksPaymentLink":
        invoke("GET", "/v1/payment_links/" + Json.readStringAndRemove(node, "payment_link"), node, callback);
        return;
      case "PostPaymentLinksPaymentLink":
        invoke("POST", "/v1/payment_links/" + Json.readStringAndRemove(node, "payment_link"), node, callback);
        return;
      case "GetPaymentLinksPaymentLinkLineItems":
        invoke("GET", "/v1/payment_links/" + Json.readStringAndRemove(node, "payment_link") + "/line_items", node, callback);
        return;
      case "GetPaymentMethods":
        invoke("GET", "/v1/payment_methods", node, callback);
        return;
      case "PostPaymentMethods":
        invoke("POST", "/v1/payment_methods", node, callback);
        return;
      case "GetPaymentMethodsPaymentMethod":
        invoke("GET", "/v1/payment_methods/" + Json.readStringAndRemove(node, "payment_method"), node, callback);
        return;
      case "PostPaymentMethodsPaymentMethod":
        invoke("POST", "/v1/payment_methods/" + Json.readStringAndRemove(node, "payment_method"), node, callback);
        return;
      case "PostPaymentMethodsPaymentMethodAttach":
        invoke("POST", "/v1/payment_methods/" + Json.readStringAndRemove(node, "payment_method") + "/attach", node, callback);
        return;
      case "PostPaymentMethodsPaymentMethodDetach":
        invoke("POST", "/v1/payment_methods/" + Json.readStringAndRemove(node, "payment_method") + "/detach", node, callback);
        return;
      case "GetPayouts":
        invoke("GET", "/v1/payouts", node, callback);
        return;
      case "PostPayouts":
        invoke("POST", "/v1/payouts", node, callback);
        return;
      case "GetPayoutsPayout":
        invoke("GET", "/v1/payouts/" + Json.readStringAndRemove(node, "payout"), node, callback);
        return;
      case "PostPayoutsPayout":
        invoke("POST", "/v1/payouts/" + Json.readStringAndRemove(node, "payout"), node, callback);
        return;
      case "PostPayoutsPayoutCancel":
        invoke("POST", "/v1/payouts/" + Json.readStringAndRemove(node, "payout") + "/cancel", node, callback);
        return;
      case "PostPayoutsPayoutReverse":
        invoke("POST", "/v1/payouts/" + Json.readStringAndRemove(node, "payout") + "/reverse", node, callback);
        return;
      case "GetPlans":
        invoke("GET", "/v1/plans", node, callback);
        return;
      case "PostPlans":
        invoke("POST", "/v1/plans", node, callback);
        return;
      case "DeletePlansPlan":
        invoke("DELETE", "/v1/plans/" + Json.readStringAndRemove(node, "plan"), node, callback);
        return;
      case "GetPlansPlan":
        invoke("GET", "/v1/plans/" + Json.readStringAndRemove(node, "plan"), node, callback);
        return;
      case "PostPlansPlan":
        invoke("POST", "/v1/plans/" + Json.readStringAndRemove(node, "plan"), node, callback);
        return;
      case "GetPrices":
        invoke("GET", "/v1/prices", node, callback);
        return;
      case "PostPrices":
        invoke("POST", "/v1/prices", node, callback);
        return;
      case "GetPricesSearch":
        invoke("GET", "/v1/prices/search", node, callback);
        return;
      case "GetPricesPrice":
        invoke("GET", "/v1/prices/" + Json.readStringAndRemove(node, "price"), node, callback);
        return;
      case "PostPricesPrice":
        invoke("POST", "/v1/prices/" + Json.readStringAndRemove(node, "price"), node, callback);
        return;
      case "GetProducts":
        invoke("GET", "/v1/products", node, callback);
        return;
      case "PostProducts":
        invoke("POST", "/v1/products", node, callback);
        return;
      case "GetProductsSearch":
        invoke("GET", "/v1/products/search", node, callback);
        return;
      case "DeleteProductsId":
        invoke("DELETE", "/v1/products/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetProductsId":
        invoke("GET", "/v1/products/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostProductsId":
        invoke("POST", "/v1/products/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetPromotionCodes":
        invoke("GET", "/v1/promotion_codes", node, callback);
        return;
      case "PostPromotionCodes":
        invoke("POST", "/v1/promotion_codes", node, callback);
        return;
      case "GetPromotionCodesPromotionCode":
        invoke("GET", "/v1/promotion_codes/" + Json.readStringAndRemove(node, "promotion_code"), node, callback);
        return;
      case "PostPromotionCodesPromotionCode":
        invoke("POST", "/v1/promotion_codes/" + Json.readStringAndRemove(node, "promotion_code"), node, callback);
        return;
      case "GetQuotes":
        invoke("GET", "/v1/quotes", node, callback);
        return;
      case "PostQuotes":
        invoke("POST", "/v1/quotes", node, callback);
        return;
      case "GetQuotesQuote":
        invoke("GET", "/v1/quotes/" + Json.readStringAndRemove(node, "quote"), node, callback);
        return;
      case "PostQuotesQuote":
        invoke("POST", "/v1/quotes/" + Json.readStringAndRemove(node, "quote"), node, callback);
        return;
      case "PostQuotesQuoteAccept":
        invoke("POST", "/v1/quotes/" + Json.readStringAndRemove(node, "quote") + "/accept", node, callback);
        return;
      case "PostQuotesQuoteCancel":
        invoke("POST", "/v1/quotes/" + Json.readStringAndRemove(node, "quote") + "/cancel", node, callback);
        return;
      case "GetQuotesQuoteComputedUpfrontLineItems":
        invoke("GET", "/v1/quotes/" + Json.readStringAndRemove(node, "quote") + "/computed_upfront_line_items", node, callback);
        return;
      case "PostQuotesQuoteFinalize":
        invoke("POST", "/v1/quotes/" + Json.readStringAndRemove(node, "quote") + "/finalize", node, callback);
        return;
      case "GetQuotesQuoteLineItems":
        invoke("GET", "/v1/quotes/" + Json.readStringAndRemove(node, "quote") + "/line_items", node, callback);
        return;
      case "GetQuotesQuotePdf":
        invoke("GET", "/v1/quotes/" + Json.readStringAndRemove(node, "quote") + "/pdf", node, callback);
        return;
      case "GetRadarEarlyFraudWarnings":
        invoke("GET", "/v1/radar/early_fraud_warnings", node, callback);
        return;
      case "GetRadarEarlyFraudWarningsEarlyFraudWarning":
        invoke("GET", "/v1/radar/early_fraud_warnings/" + Json.readStringAndRemove(node, "early_fraud_warning"), node, callback);
        return;
      case "GetRadarValueListItems":
        invoke("GET", "/v1/radar/value_list_items", node, callback);
        return;
      case "PostRadarValueListItems":
        invoke("POST", "/v1/radar/value_list_items", node, callback);
        return;
      case "DeleteRadarValueListItemsItem":
        invoke("DELETE", "/v1/radar/value_list_items/" + Json.readStringAndRemove(node, "item"), node, callback);
        return;
      case "GetRadarValueListItemsItem":
        invoke("GET", "/v1/radar/value_list_items/" + Json.readStringAndRemove(node, "item"), node, callback);
        return;
      case "GetRadarValueLists":
        invoke("GET", "/v1/radar/value_lists", node, callback);
        return;
      case "PostRadarValueLists":
        invoke("POST", "/v1/radar/value_lists", node, callback);
        return;
      case "DeleteRadarValueListsValueList":
        invoke("DELETE", "/v1/radar/value_lists/" + Json.readStringAndRemove(node, "value_list"), node, callback);
        return;
      case "GetRadarValueListsValueList":
        invoke("GET", "/v1/radar/value_lists/" + Json.readStringAndRemove(node, "value_list"), node, callback);
        return;
      case "PostRadarValueListsValueList":
        invoke("POST", "/v1/radar/value_lists/" + Json.readStringAndRemove(node, "value_list"), node, callback);
        return;
      case "GetRefunds":
        invoke("GET", "/v1/refunds", node, callback);
        return;
      case "PostRefunds":
        invoke("POST", "/v1/refunds", node, callback);
        return;
      case "GetRefundsRefund":
        invoke("GET", "/v1/refunds/" + Json.readStringAndRemove(node, "refund"), node, callback);
        return;
      case "PostRefundsRefund":
        invoke("POST", "/v1/refunds/" + Json.readStringAndRemove(node, "refund"), node, callback);
        return;
      case "PostRefundsRefundCancel":
        invoke("POST", "/v1/refunds/" + Json.readStringAndRemove(node, "refund") + "/cancel", node, callback);
        return;
      case "GetReportingReportRuns":
        invoke("GET", "/v1/reporting/report_runs", node, callback);
        return;
      case "PostReportingReportRuns":
        invoke("POST", "/v1/reporting/report_runs", node, callback);
        return;
      case "GetReportingReportRunsReportRun":
        invoke("GET", "/v1/reporting/report_runs/" + Json.readStringAndRemove(node, "report_run"), node, callback);
        return;
      case "GetReportingReportTypes":
        invoke("GET", "/v1/reporting/report_types", node, callback);
        return;
      case "GetReportingReportTypesReportType":
        invoke("GET", "/v1/reporting/report_types/" + Json.readStringAndRemove(node, "report_type"), node, callback);
        return;
      case "GetReviews":
        invoke("GET", "/v1/reviews", node, callback);
        return;
      case "GetReviewsReview":
        invoke("GET", "/v1/reviews/" + Json.readStringAndRemove(node, "review"), node, callback);
        return;
      case "PostReviewsReviewApprove":
        invoke("POST", "/v1/reviews/" + Json.readStringAndRemove(node, "review") + "/approve", node, callback);
        return;
      case "GetSetupAttempts":
        invoke("GET", "/v1/setup_attempts", node, callback);
        return;
      case "GetSetupIntents":
        invoke("GET", "/v1/setup_intents", node, callback);
        return;
      case "PostSetupIntents":
        invoke("POST", "/v1/setup_intents", node, callback);
        return;
      case "GetSetupIntentsIntent":
        invoke("GET", "/v1/setup_intents/" + Json.readStringAndRemove(node, "intent"), node, callback);
        return;
      case "PostSetupIntentsIntent":
        invoke("POST", "/v1/setup_intents/" + Json.readStringAndRemove(node, "intent"), node, callback);
        return;
      case "PostSetupIntentsIntentCancel":
        invoke("POST", "/v1/setup_intents/" + Json.readStringAndRemove(node, "intent") + "/cancel", node, callback);
        return;
      case "PostSetupIntentsIntentConfirm":
        invoke("POST", "/v1/setup_intents/" + Json.readStringAndRemove(node, "intent") + "/confirm", node, callback);
        return;
      case "PostSetupIntentsIntentVerifyMicrodeposits":
        invoke("POST", "/v1/setup_intents/" + Json.readStringAndRemove(node, "intent") + "/verify_microdeposits", node, callback);
        return;
      case "GetShippingRates":
        invoke("GET", "/v1/shipping_rates", node, callback);
        return;
      case "PostShippingRates":
        invoke("POST", "/v1/shipping_rates", node, callback);
        return;
      case "GetShippingRatesShippingRateToken":
        invoke("GET", "/v1/shipping_rates/" + Json.readStringAndRemove(node, "shipping_rate_token"), node, callback);
        return;
      case "PostShippingRatesShippingRateToken":
        invoke("POST", "/v1/shipping_rates/" + Json.readStringAndRemove(node, "shipping_rate_token"), node, callback);
        return;
      case "GetSigmaScheduledQueryRuns":
        invoke("GET", "/v1/sigma/scheduled_query_runs", node, callback);
        return;
      case "GetSigmaScheduledQueryRunsScheduledQueryRun":
        invoke("GET", "/v1/sigma/scheduled_query_runs/" + Json.readStringAndRemove(node, "scheduled_query_run"), node, callback);
        return;
      case "PostSources":
        invoke("POST", "/v1/sources", node, callback);
        return;
      case "GetSourcesSource":
        invoke("GET", "/v1/sources/" + Json.readStringAndRemove(node, "source"), node, callback);
        return;
      case "PostSourcesSource":
        invoke("POST", "/v1/sources/" + Json.readStringAndRemove(node, "source"), node, callback);
        return;
      case "GetSourcesSourceMandateNotificationsMandateNotification":
        invoke("GET", "/v1/sources/" + Json.readStringAndRemove(node, "source") + "/mandate_notifications/" + Json.readStringAndRemove(node, "mandate_notification"), node, callback);
        return;
      case "GetSourcesSourceSourceTransactions":
        invoke("GET", "/v1/sources/" + Json.readStringAndRemove(node, "source") + "/source_transactions", node, callback);
        return;
      case "GetSourcesSourceSourceTransactionsSourceTransaction":
        invoke("GET", "/v1/sources/" + Json.readStringAndRemove(node, "source") + "/source_transactions/" + Json.readStringAndRemove(node, "source_transaction"), node, callback);
        return;
      case "PostSourcesSourceVerify":
        invoke("POST", "/v1/sources/" + Json.readStringAndRemove(node, "source") + "/verify", node, callback);
        return;
      case "GetSubscriptionItems":
        invoke("GET", "/v1/subscription_items", node, callback);
        return;
      case "PostSubscriptionItems":
        invoke("POST", "/v1/subscription_items", node, callback);
        return;
      case "DeleteSubscriptionItemsItem":
        invoke("DELETE", "/v1/subscription_items/" + Json.readStringAndRemove(node, "item"), node, callback);
        return;
      case "GetSubscriptionItemsItem":
        invoke("GET", "/v1/subscription_items/" + Json.readStringAndRemove(node, "item"), node, callback);
        return;
      case "PostSubscriptionItemsItem":
        invoke("POST", "/v1/subscription_items/" + Json.readStringAndRemove(node, "item"), node, callback);
        return;
      case "GetSubscriptionItemsSubscriptionItemUsageRecordSummaries":
        invoke("GET", "/v1/subscription_items/" + Json.readStringAndRemove(node, "subscription_item") + "/usage_record_summaries", node, callback);
        return;
      case "PostSubscriptionItemsSubscriptionItemUsageRecords":
        invoke("POST", "/v1/subscription_items/" + Json.readStringAndRemove(node, "subscription_item") + "/usage_records", node, callback);
        return;
      case "GetSubscriptionSchedules":
        invoke("GET", "/v1/subscription_schedules", node, callback);
        return;
      case "PostSubscriptionSchedules":
        invoke("POST", "/v1/subscription_schedules", node, callback);
        return;
      case "GetSubscriptionSchedulesSchedule":
        invoke("GET", "/v1/subscription_schedules/" + Json.readStringAndRemove(node, "schedule"), node, callback);
        return;
      case "PostSubscriptionSchedulesSchedule":
        invoke("POST", "/v1/subscription_schedules/" + Json.readStringAndRemove(node, "schedule"), node, callback);
        return;
      case "PostSubscriptionSchedulesScheduleCancel":
        invoke("POST", "/v1/subscription_schedules/" + Json.readStringAndRemove(node, "schedule") + "/cancel", node, callback);
        return;
      case "PostSubscriptionSchedulesScheduleRelease":
        invoke("POST", "/v1/subscription_schedules/" + Json.readStringAndRemove(node, "schedule") + "/release", node, callback);
        return;
      case "GetSubscriptions":
        invoke("GET", "/v1/subscriptions", node, callback);
        return;
      case "PostSubscriptions":
        invoke("POST", "/v1/subscriptions", node, callback);
        return;
      case "GetSubscriptionsSearch":
        invoke("GET", "/v1/subscriptions/search", node, callback);
        return;
      case "DeleteSubscriptionsSubscriptionExposedId":
        invoke("DELETE", "/v1/subscriptions/" + Json.readStringAndRemove(node, "subscription_exposed_id"), node, callback);
        return;
      case "GetSubscriptionsSubscriptionExposedId":
        invoke("GET", "/v1/subscriptions/" + Json.readStringAndRemove(node, "subscription_exposed_id"), node, callback);
        return;
      case "PostSubscriptionsSubscriptionExposedId":
        invoke("POST", "/v1/subscriptions/" + Json.readStringAndRemove(node, "subscription_exposed_id"), node, callback);
        return;
      case "DeleteSubscriptionsSubscriptionExposedIdDiscount":
        invoke("DELETE", "/v1/subscriptions/" + Json.readStringAndRemove(node, "subscription_exposed_id") + "/discount", node, callback);
        return;
      case "PostSubscriptionsSubscriptionResume":
        invoke("POST", "/v1/subscriptions/" + Json.readStringAndRemove(node, "subscription") + "/resume", node, callback);
        return;
      case "PostTaxCalculations":
        invoke("POST", "/v1/tax/calculations", node, callback);
        return;
      case "GetTaxCalculationsCalculationLineItems":
        invoke("GET", "/v1/tax/calculations/" + Json.readStringAndRemove(node, "calculation") + "/line_items", node, callback);
        return;
      case "PostTaxTransactionsCreateFromCalculation":
        invoke("POST", "/v1/tax/transactions/create_from_calculation", node, callback);
        return;
      case "PostTaxTransactionsCreateReversal":
        invoke("POST", "/v1/tax/transactions/create_reversal", node, callback);
        return;
      case "GetTaxTransactionsTransaction":
        invoke("GET", "/v1/tax/transactions/" + Json.readStringAndRemove(node, "transaction"), node, callback);
        return;
      case "GetTaxTransactionsTransactionLineItems":
        invoke("GET", "/v1/tax/transactions/" + Json.readStringAndRemove(node, "transaction") + "/line_items", node, callback);
        return;
      case "GetTaxCodes":
        invoke("GET", "/v1/tax_codes", node, callback);
        return;
      case "GetTaxCodesId":
        invoke("GET", "/v1/tax_codes/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetTaxRates":
        invoke("GET", "/v1/tax_rates", node, callback);
        return;
      case "PostTaxRates":
        invoke("POST", "/v1/tax_rates", node, callback);
        return;
      case "GetTaxRatesTaxRate":
        invoke("GET", "/v1/tax_rates/" + Json.readStringAndRemove(node, "tax_rate"), node, callback);
        return;
      case "PostTaxRatesTaxRate":
        invoke("POST", "/v1/tax_rates/" + Json.readStringAndRemove(node, "tax_rate"), node, callback);
        return;
      case "GetTerminalConfigurations":
        invoke("GET", "/v1/terminal/configurations", node, callback);
        return;
      case "PostTerminalConfigurations":
        invoke("POST", "/v1/terminal/configurations", node, callback);
        return;
      case "DeleteTerminalConfigurationsConfiguration":
        invoke("DELETE", "/v1/terminal/configurations/" + Json.readStringAndRemove(node, "configuration"), node, callback);
        return;
      case "GetTerminalConfigurationsConfiguration":
        invoke("GET", "/v1/terminal/configurations/" + Json.readStringAndRemove(node, "configuration"), node, callback);
        return;
      case "PostTerminalConfigurationsConfiguration":
        invoke("POST", "/v1/terminal/configurations/" + Json.readStringAndRemove(node, "configuration"), node, callback);
        return;
      case "PostTerminalConnectionTokens":
        invoke("POST", "/v1/terminal/connection_tokens", node, callback);
        return;
      case "GetTerminalLocations":
        invoke("GET", "/v1/terminal/locations", node, callback);
        return;
      case "PostTerminalLocations":
        invoke("POST", "/v1/terminal/locations", node, callback);
        return;
      case "DeleteTerminalLocationsLocation":
        invoke("DELETE", "/v1/terminal/locations/" + Json.readStringAndRemove(node, "location"), node, callback);
        return;
      case "GetTerminalLocationsLocation":
        invoke("GET", "/v1/terminal/locations/" + Json.readStringAndRemove(node, "location"), node, callback);
        return;
      case "PostTerminalLocationsLocation":
        invoke("POST", "/v1/terminal/locations/" + Json.readStringAndRemove(node, "location"), node, callback);
        return;
      case "GetTerminalReaders":
        invoke("GET", "/v1/terminal/readers", node, callback);
        return;
      case "PostTerminalReaders":
        invoke("POST", "/v1/terminal/readers", node, callback);
        return;
      case "DeleteTerminalReadersReader":
        invoke("DELETE", "/v1/terminal/readers/" + Json.readStringAndRemove(node, "reader"), node, callback);
        return;
      case "GetTerminalReadersReader":
        invoke("GET", "/v1/terminal/readers/" + Json.readStringAndRemove(node, "reader"), node, callback);
        return;
      case "PostTerminalReadersReader":
        invoke("POST", "/v1/terminal/readers/" + Json.readStringAndRemove(node, "reader"), node, callback);
        return;
      case "PostTerminalReadersReaderCancelAction":
        invoke("POST", "/v1/terminal/readers/" + Json.readStringAndRemove(node, "reader") + "/cancel_action", node, callback);
        return;
      case "PostTerminalReadersReaderProcessPaymentIntent":
        invoke("POST", "/v1/terminal/readers/" + Json.readStringAndRemove(node, "reader") + "/process_payment_intent", node, callback);
        return;
      case "PostTerminalReadersReaderProcessSetupIntent":
        invoke("POST", "/v1/terminal/readers/" + Json.readStringAndRemove(node, "reader") + "/process_setup_intent", node, callback);
        return;
      case "PostTerminalReadersReaderRefundPayment":
        invoke("POST", "/v1/terminal/readers/" + Json.readStringAndRemove(node, "reader") + "/refund_payment", node, callback);
        return;
      case "PostTerminalReadersReaderSetReaderDisplay":
        invoke("POST", "/v1/terminal/readers/" + Json.readStringAndRemove(node, "reader") + "/set_reader_display", node, callback);
        return;
      case "PostTestHelpersCustomersCustomerFundCashBalance":
        invoke("POST", "/v1/test_helpers/customers/" + Json.readStringAndRemove(node, "customer") + "/fund_cash_balance", node, callback);
        return;
      case "PostTestHelpersIssuingCardsCardShippingDeliver":
        invoke("POST", "/v1/test_helpers/issuing/cards/" + Json.readStringAndRemove(node, "card") + "/shipping/deliver", node, callback);
        return;
      case "PostTestHelpersIssuingCardsCardShippingFail":
        invoke("POST", "/v1/test_helpers/issuing/cards/" + Json.readStringAndRemove(node, "card") + "/shipping/fail", node, callback);
        return;
      case "PostTestHelpersIssuingCardsCardShippingReturn":
        invoke("POST", "/v1/test_helpers/issuing/cards/" + Json.readStringAndRemove(node, "card") + "/shipping/return", node, callback);
        return;
      case "PostTestHelpersIssuingCardsCardShippingShip":
        invoke("POST", "/v1/test_helpers/issuing/cards/" + Json.readStringAndRemove(node, "card") + "/shipping/ship", node, callback);
        return;
      case "PostTestHelpersRefundsRefundExpire":
        invoke("POST", "/v1/test_helpers/refunds/" + Json.readStringAndRemove(node, "refund") + "/expire", node, callback);
        return;
      case "PostTestHelpersTerminalReadersReaderPresentPaymentMethod":
        invoke("POST", "/v1/test_helpers/terminal/readers/" + Json.readStringAndRemove(node, "reader") + "/present_payment_method", node, callback);
        return;
      case "GetTestHelpersTestClocks":
        invoke("GET", "/v1/test_helpers/test_clocks", node, callback);
        return;
      case "PostTestHelpersTestClocks":
        invoke("POST", "/v1/test_helpers/test_clocks", node, callback);
        return;
      case "DeleteTestHelpersTestClocksTestClock":
        invoke("DELETE", "/v1/test_helpers/test_clocks/" + Json.readStringAndRemove(node, "test_clock"), node, callback);
        return;
      case "GetTestHelpersTestClocksTestClock":
        invoke("GET", "/v1/test_helpers/test_clocks/" + Json.readStringAndRemove(node, "test_clock"), node, callback);
        return;
      case "PostTestHelpersTestClocksTestClockAdvance":
        invoke("POST", "/v1/test_helpers/test_clocks/" + Json.readStringAndRemove(node, "test_clock") + "/advance", node, callback);
        return;
      case "PostTestHelpersTreasuryInboundTransfersIdFail":
        invoke("POST", "/v1/test_helpers/treasury/inbound_transfers/" + Json.readStringAndRemove(node, "id") + "/fail", node, callback);
        return;
      case "PostTestHelpersTreasuryInboundTransfersIdReturn":
        invoke("POST", "/v1/test_helpers/treasury/inbound_transfers/" + Json.readStringAndRemove(node, "id") + "/return", node, callback);
        return;
      case "PostTestHelpersTreasuryInboundTransfersIdSucceed":
        invoke("POST", "/v1/test_helpers/treasury/inbound_transfers/" + Json.readStringAndRemove(node, "id") + "/succeed", node, callback);
        return;
      case "PostTestHelpersTreasuryOutboundPaymentsIdFail":
        invoke("POST", "/v1/test_helpers/treasury/outbound_payments/" + Json.readStringAndRemove(node, "id") + "/fail", node, callback);
        return;
      case "PostTestHelpersTreasuryOutboundPaymentsIdPost":
        invoke("POST", "/v1/test_helpers/treasury/outbound_payments/" + Json.readStringAndRemove(node, "id") + "/post", node, callback);
        return;
      case "PostTestHelpersTreasuryOutboundPaymentsIdReturn":
        invoke("POST", "/v1/test_helpers/treasury/outbound_payments/" + Json.readStringAndRemove(node, "id") + "/return", node, callback);
        return;
      case "PostTestHelpersTreasuryOutboundTransfersOutboundTransferFail":
        invoke("POST", "/v1/test_helpers/treasury/outbound_transfers/" + Json.readStringAndRemove(node, "outbound_transfer") + "/fail", node, callback);
        return;
      case "PostTestHelpersTreasuryOutboundTransfersOutboundTransferPost":
        invoke("POST", "/v1/test_helpers/treasury/outbound_transfers/" + Json.readStringAndRemove(node, "outbound_transfer") + "/post", node, callback);
        return;
      case "PostTestHelpersTreasuryOutboundTransfersOutboundTransferReturn":
        invoke("POST", "/v1/test_helpers/treasury/outbound_transfers/" + Json.readStringAndRemove(node, "outbound_transfer") + "/return", node, callback);
        return;
      case "PostTestHelpersTreasuryReceivedCredits":
        invoke("POST", "/v1/test_helpers/treasury/received_credits", node, callback);
        return;
      case "PostTestHelpersTreasuryReceivedDebits":
        invoke("POST", "/v1/test_helpers/treasury/received_debits", node, callback);
        return;
      case "PostTokens":
        invoke("POST", "/v1/tokens", node, callback);
        return;
      case "GetTokensToken":
        invoke("GET", "/v1/tokens/" + Json.readStringAndRemove(node, "token"), node, callback);
        return;
      case "GetTopups":
        invoke("GET", "/v1/topups", node, callback);
        return;
      case "PostTopups":
        invoke("POST", "/v1/topups", node, callback);
        return;
      case "GetTopupsTopup":
        invoke("GET", "/v1/topups/" + Json.readStringAndRemove(node, "topup"), node, callback);
        return;
      case "PostTopupsTopup":
        invoke("POST", "/v1/topups/" + Json.readStringAndRemove(node, "topup"), node, callback);
        return;
      case "PostTopupsTopupCancel":
        invoke("POST", "/v1/topups/" + Json.readStringAndRemove(node, "topup") + "/cancel", node, callback);
        return;
      case "GetTransfers":
        invoke("GET", "/v1/transfers", node, callback);
        return;
      case "PostTransfers":
        invoke("POST", "/v1/transfers", node, callback);
        return;
      case "GetTransfersIdReversals":
        invoke("GET", "/v1/transfers/" + Json.readStringAndRemove(node, "id") + "/reversals", node, callback);
        return;
      case "PostTransfersIdReversals":
        invoke("POST", "/v1/transfers/" + Json.readStringAndRemove(node, "id") + "/reversals", node, callback);
        return;
      case "GetTransfersTransfer":
        invoke("GET", "/v1/transfers/" + Json.readStringAndRemove(node, "transfer"), node, callback);
        return;
      case "PostTransfersTransfer":
        invoke("POST", "/v1/transfers/" + Json.readStringAndRemove(node, "transfer"), node, callback);
        return;
      case "GetTransfersTransferReversalsId":
        invoke("GET", "/v1/transfers/" + Json.readStringAndRemove(node, "transfer") + "/reversals/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostTransfersTransferReversalsId":
        invoke("POST", "/v1/transfers/" + Json.readStringAndRemove(node, "transfer") + "/reversals/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetTreasuryCreditReversals":
        invoke("GET", "/v1/treasury/credit_reversals", node, callback);
        return;
      case "PostTreasuryCreditReversals":
        invoke("POST", "/v1/treasury/credit_reversals", node, callback);
        return;
      case "GetTreasuryCreditReversalsCreditReversal":
        invoke("GET", "/v1/treasury/credit_reversals/" + Json.readStringAndRemove(node, "credit_reversal"), node, callback);
        return;
      case "GetTreasuryDebitReversals":
        invoke("GET", "/v1/treasury/debit_reversals", node, callback);
        return;
      case "PostTreasuryDebitReversals":
        invoke("POST", "/v1/treasury/debit_reversals", node, callback);
        return;
      case "GetTreasuryDebitReversalsDebitReversal":
        invoke("GET", "/v1/treasury/debit_reversals/" + Json.readStringAndRemove(node, "debit_reversal"), node, callback);
        return;
      case "GetTreasuryFinancialAccounts":
        invoke("GET", "/v1/treasury/financial_accounts", node, callback);
        return;
      case "PostTreasuryFinancialAccounts":
        invoke("POST", "/v1/treasury/financial_accounts", node, callback);
        return;
      case "GetTreasuryFinancialAccountsFinancialAccount":
        invoke("GET", "/v1/treasury/financial_accounts/" + Json.readStringAndRemove(node, "financial_account"), node, callback);
        return;
      case "PostTreasuryFinancialAccountsFinancialAccount":
        invoke("POST", "/v1/treasury/financial_accounts/" + Json.readStringAndRemove(node, "financial_account"), node, callback);
        return;
      case "GetTreasuryFinancialAccountsFinancialAccountFeatures":
        invoke("GET", "/v1/treasury/financial_accounts/" + Json.readStringAndRemove(node, "financial_account") + "/features", node, callback);
        return;
      case "PostTreasuryFinancialAccountsFinancialAccountFeatures":
        invoke("POST", "/v1/treasury/financial_accounts/" + Json.readStringAndRemove(node, "financial_account") + "/features", node, callback);
        return;
      case "GetTreasuryInboundTransfers":
        invoke("GET", "/v1/treasury/inbound_transfers", node, callback);
        return;
      case "PostTreasuryInboundTransfers":
        invoke("POST", "/v1/treasury/inbound_transfers", node, callback);
        return;
      case "GetTreasuryInboundTransfersId":
        invoke("GET", "/v1/treasury/inbound_transfers/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostTreasuryInboundTransfersInboundTransferCancel":
        invoke("POST", "/v1/treasury/inbound_transfers/" + Json.readStringAndRemove(node, "inbound_transfer") + "/cancel", node, callback);
        return;
      case "GetTreasuryOutboundPayments":
        invoke("GET", "/v1/treasury/outbound_payments", node, callback);
        return;
      case "PostTreasuryOutboundPayments":
        invoke("POST", "/v1/treasury/outbound_payments", node, callback);
        return;
      case "GetTreasuryOutboundPaymentsId":
        invoke("GET", "/v1/treasury/outbound_payments/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "PostTreasuryOutboundPaymentsIdCancel":
        invoke("POST", "/v1/treasury/outbound_payments/" + Json.readStringAndRemove(node, "id") + "/cancel", node, callback);
        return;
      case "GetTreasuryOutboundTransfers":
        invoke("GET", "/v1/treasury/outbound_transfers", node, callback);
        return;
      case "PostTreasuryOutboundTransfers":
        invoke("POST", "/v1/treasury/outbound_transfers", node, callback);
        return;
      case "GetTreasuryOutboundTransfersOutboundTransfer":
        invoke("GET", "/v1/treasury/outbound_transfers/" + Json.readStringAndRemove(node, "outbound_transfer"), node, callback);
        return;
      case "PostTreasuryOutboundTransfersOutboundTransferCancel":
        invoke("POST", "/v1/treasury/outbound_transfers/" + Json.readStringAndRemove(node, "outbound_transfer") + "/cancel", node, callback);
        return;
      case "GetTreasuryReceivedCredits":
        invoke("GET", "/v1/treasury/received_credits", node, callback);
        return;
      case "GetTreasuryReceivedCreditsId":
        invoke("GET", "/v1/treasury/received_credits/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetTreasuryReceivedDebits":
        invoke("GET", "/v1/treasury/received_debits", node, callback);
        return;
      case "GetTreasuryReceivedDebitsId":
        invoke("GET", "/v1/treasury/received_debits/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetTreasuryTransactionEntries":
        invoke("GET", "/v1/treasury/transaction_entries", node, callback);
        return;
      case "GetTreasuryTransactionEntriesId":
        invoke("GET", "/v1/treasury/transaction_entries/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetTreasuryTransactions":
        invoke("GET", "/v1/treasury/transactions", node, callback);
        return;
      case "GetTreasuryTransactionsId":
        invoke("GET", "/v1/treasury/transactions/" + Json.readStringAndRemove(node, "id"), node, callback);
        return;
      case "GetWebhookEndpoints":
        invoke("GET", "/v1/webhook_endpoints", node, callback);
        return;
      case "PostWebhookEndpoints":
        invoke("POST", "/v1/webhook_endpoints", node, callback);
        return;
      case "DeleteWebhookEndpointsWebhookEndpoint":
        invoke("DELETE", "/v1/webhook_endpoints/" + Json.readStringAndRemove(node, "webhook_endpoint"), node, callback);
        return;
      case "GetWebhookEndpointsWebhookEndpoint":
        invoke("GET", "/v1/webhook_endpoints/" + Json.readStringAndRemove(node, "webhook_endpoint"), node, callback);
        return;
      case "PostWebhookEndpointsWebhookEndpoint":
        invoke("POST", "/v1/webhook_endpoints/" + Json.readStringAndRemove(node, "webhook_endpoint"), node, callback);
        return;
/** END[CODEGEN-METHODS] **/
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }
}
