import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { Container, Card, Spinner, Alert } from "react-bootstrap";

const PaymentReturn = () => {
    const location = useLocation();
    const [loading, setLoading] = useState(true);
    const [result, setResult] = useState(null);

    useEffect(() => {
        const searchParams = new URLSearchParams(location.search);
        const paramsObj = {};
        for (const [key, value] of searchParams.entries()) {
            paramsObj[key] = value;
        }

        let paymentResult = null;

        if (paramsObj["transactionCode"]) {
            paymentResult = {
                success: paramsObj["status"] === "SUCCESS",
                transactionCode: paramsObj["transactionCode"],
                paymentStatus: paramsObj["status"],
                message:
                    paramsObj["status"] === "SUCCESS"
                        ? "Thanh toán VNPay thành công!"
                        : "Thanh toán VNPay thất bại!",
            };
        } else if (paramsObj["orderId"]) {
            paymentResult = {
                success: paramsObj["resultCode"] === "0",
                transactionCode: paramsObj["orderId"],
                paymentStatus: paramsObj["resultCode"] === "0" ? "SUCCESS" : "FAILED",
                message:
                    paramsObj["resultCode"] === "0"
                        ? "Thanh toán MoMo thành công!"
                        : "Thanh toán MoMo thất bại!",
            };
        }

        setResult(paymentResult || { success: false, message: "Không có dữ liệu thanh toán!" });
        setLoading(false);
    }, [location]);

    return (
        <Container className="mt-5">
            <Card className="p-4 shadow-sm text-center">
                {loading ? (
                    <Spinner animation="border" />
                ) : result?.success ? (
                    <Alert variant="success">
                        <h4>Thanh toán thành công 🎉</h4>
                        <p>Mã giao dịch: {result.transactionCode}</p>
                        <p>Trạng thái: {result.paymentStatus}</p>
                        <p>{result.message}</p>
                    </Alert>
                ) : (
                    <Alert variant="danger">
                        <h4>Thanh toán thất bại ❌</h4>
                        <p>{result?.message || "Vui lòng thử lại."}</p>
                    </Alert>
                )}
            </Card>
        </Container>
    );
};

export default PaymentReturn;
