package JPA_Project.service;

/**
 * Service thanh toán - Xử lý nghiệp vụ thanh toán.
 */
public class PaymentService {

    public enum PaymentMethod {
        CASH,
        BANK_TRANSFER,
        CREDIT_CARD,
        E_WALLET
    }

    public PaymentResult processPayment(double amount, PaymentMethod method, String transactionId) {
        if (amount <= 0) {
            return new PaymentResult(false, "Số tiền không hợp lệ");
        }

        try {
            switch (method) {
                case CASH:
                    return processCashPayment(amount);
                case BANK_TRANSFER:
                    return processBankTransfer(amount, transactionId);
                case CREDIT_CARD:
                    return processCreditCard(amount, transactionId);
                case E_WALLET:
                    return processEWallet(amount, transactionId);
                default:
                    return new PaymentResult(false, "Phương thức thanh toán không hỗ trợ");
            }
        } catch (Exception e) {
            return new PaymentResult(false, "Lỗi xử lý: " + e.getMessage());
        }
    }

    private PaymentResult processCashPayment(double amount) {
        return new PaymentResult(true, "Thanh toán tiền mặt thành công");
    }

    private PaymentResult processBankTransfer(double amount, String transactionId) {
        return new PaymentResult(true, "Chuyển khoản thành công. Mã giao dịch: " + transactionId);
    }

    private PaymentResult processCreditCard(double amount, String transactionId) {
        return new PaymentResult(true, "Thanh toán thẻ thành công. Mã giao dịch: " + transactionId);
    }

    private PaymentResult processEWallet(double amount, String transactionId) {
        return new PaymentResult(true, "Thanh toán ví điện tử thành công. Mã giao dịch: " + transactionId);
    }

    public static class PaymentResult {
        private final boolean success;
        private final String message;

        public PaymentResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
