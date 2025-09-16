import { useEffect, useState } from "react";
import { Container, Form, Button, Spinner, Alert } from "react-bootstrap";
import { useNavigate, useSearchParams } from "react-router-dom";
import { endpoints, authApis } from "../configs/Apis";
import cookie from "react-cookies";
import { toast } from "react-toastify";

const Payment = () => {
    const [searchParams] = useSearchParams();
    const postId = searchParams.get("postId");

    const [post, setPost] = useState(null);
    const [transactionTypes, setTransactionTypes] = useState([]);
    const [form, setForm] = useState({ slotQuantity: 1, transactionType: "" });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const navigate = useNavigate();
    const token = cookie.load("token");

    useEffect(() => {
        const loadData = async () => {
            try {
                const [typesRes, postRes] = await Promise.all([
                    authApis(token).get(endpoints["payment-methods"]),
                    authApis(token).get(`${endpoints["service-post-detail"]}/${postId}`),
                ]);
                setTransactionTypes(typesRes.data || []);
                setPost(postRes.data);
                setForm(f => ({
                    ...f,
                    transactionType: typesRes.data[0] || "",
                }));
            } catch (err) {
                console.error("Lỗi khi tải dữ liệu:", err);
                setError("Không thể tải dữ liệu thanh toán!");
            }
        };
        loadData();
    }, [postId, token]);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");

        try {
            const res = await authApis(token).post(endpoints["payment"](postId), {
                slotQuantity: Number(form.slotQuantity),
                transactionType: form.transactionType
            });

            const { payUrl } = res.data || {};
            if (payUrl) {
                toast.success("Chuyển hướng đến cổng thanh toán...");
                window.location.href = payUrl;
            } else {
                toast.error("Không nhận được đường dẫn thanh toán!");
            }
        } catch (err) {
            const msg = err?.response?.data || "Thanh toán thất bại!";
            toast.error(msg);
            setError(msg);
        }
        setLoading(false);
    };

    if (error) return <Alert variant="danger">{error}</Alert>;
    if (!post) return <Spinner animation="border" />;

    return (
        <Container className="my-5">
            <h3 className="text-center">Thanh toán dịch vụ</h3>
            <p><strong>Dịch vụ:</strong> {post.name}</p>
            <p><strong>Giá:</strong> {Number(post.price).toLocaleString("vi-VN")} VNĐ</p>
            <p><strong>Slot còn lại:</strong> {post.availableSlot}</p>

            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>Số lượng vé</Form.Label>
                    <Form.Control
                        type="number"
                        name="slotQuantity"
                        value={form.slotQuantity}
                        onChange={handleChange}
                        min={1}
                        max={post.availableSlot}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Phương thức thanh toán</Form.Label>
                    <Form.Select
                        name="transactionType"
                        value={form.transactionType}
                        onChange={handleChange}
                        required
                    >
                        {transactionTypes.map((t, i) => (
                            <option key={i} value={t}>{t}</option>
                        ))}
                    </Form.Select>
                </Form.Group>

                <Button type="submit" variant="success" disabled={loading}>
                    {loading ? "Đang xử lý..." : "Thanh toán ngay"}
                </Button>
            </Form>
        </Container>
    );
};

export default Payment;
