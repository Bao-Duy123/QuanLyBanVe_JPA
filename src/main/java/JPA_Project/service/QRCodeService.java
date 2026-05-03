package JPA_Project.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * QRCodeService - Service xử lý tạo mã QR cho vé tàu.
 * Sử dụng thư viện ZXing (com.google.zxing).
 */
public class QRCodeService {

    /**
     * Tạo mã QR dưới dạng ảnh BufferedImage.
     *
     * @param data Dữ liệu cần mã hóa (Mã vé)
     * @param size Kích thước hình ảnh (pixel)
     * @return BufferedImage chứa mã QR
     * @throws WriterException Nếu quá trình mã hóa thất bại
     */
    public static BufferedImage generateQRCodeImage(String data, int size) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(
                data,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
        );

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Tạo mã QR với kích thước mặc định (200x200).
     *
     * @param data Dữ liệu cần mã hóa
     * @return BufferedImage chứa mã QR
     * @throws WriterException Nếu quá trình mã hóa thất bại
     */
    public static BufferedImage generateQRCodeImage(String data) throws WriterException {
        return generateQRCodeImage(data, 200);
    }
}
