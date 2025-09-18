import { useEffect, useState } from "react";
import { authApis, endpoints } from "../configs/Apis";
import cookie from "react-cookies";
import { Spinner, Table, Alert, Container, Badge, Card } from "react-bootstrap";

const UserTransaction = () => {
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState(null);
    const token = cookie.load("token");

    useEffect(() => {
        const fetchTransactions = async () => {
            try {
                const res = await authApis(token).get(endpoints["user-transactions"]);
                setTransactions(res.data);
            } catch (error) {
                console.error("Lỗi tải giao dịch:", error);
                setErr("Không thể tải danh sách giao dịch!");
            } finally {
                setLoading(false);
            }
        };

        fetchTransactions();
    }, [token]);

    const renderStatusBadge = (status) => {
        switch (status) {
            case "PAID":
                return <Badge bg="success">Đã thanh toán</Badge>;
            case "UNPAID":
                return <Badge bg="secondary">Chưa thanh toán</Badge>;
            case "FAILED":
                return <Badge bg="danger">Thất bại</Badge>;
            default:
                return <Badge bg="warning">{status}</Badge>;
        }
    };

    return (
        <Container className="mt-4">
            <Card className="shadow-sm">
                <Card.Body>
                    <h4 className="mb-4">Lịch sử giao dịch</h4>

                    {loading && (
                        <div className="text-center my-4">
                            <Spinner animation="border" variant="primary" />
                        </div>
                    )}

                    {err && <Alert variant="danger">{err}</Alert>}

                    {!loading && !err && transactions.length === 0 && (
                        <Alert variant="info">Bạn chưa có giao dịch nào.</Alert>
                    )}

                    {!loading && !err && transactions.length > 0 && (
                        <Table striped bordered hover responsive className="text-center align-middle">
                            <thead className="table-primary">
                                <tr>
                                    <th>Mã GD</th>
                                    <th>Tên dịch vụ</th>
                                    <th>Số tiền</th>
                                    <th>Phương thức</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày tạo</th>
                                </tr>
                            </thead>
                            <tbody>
                                {transactions.map((tx) => (
                                    <tr key={tx.id}>
                                        <td>{tx.transactionCode}</td>
                                        <td>{tx.serviceName}</td>
                                        <td>{tx.amount.toLocaleString()} VNĐ</td>
                                        <td>{tx.paymentMethod}</td>
                                        <td>{renderStatusBadge(tx.status)}</td>
                                        <td>{new Date(tx.createdDate).toLocaleString("vi-VN")}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    )}
                </Card.Body>
            </Card>
        </Container>
    );
};

export default UserTransaction;
