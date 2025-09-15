import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { endpoints } from "../configs/Apis";
import { Card, Row, Col, Spinner, Alert, Container, Table } from "react-bootstrap";
import { MyUserContext } from "../configs/Context";
import ProviderRating from "./ProviderRating";
import Apis from "../configs/Apis";

const ProviderDetail = () => {
    const { providerId } = useParams();
    const [provider, setProvider] = useState(null);
    const [services, setServices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [avgRating, setAvgRating] = useState(null);
    const [ratingCount, setRatingCount] = useState(0);

    const [currentUser] = useContext(MyUserContext);

    const navigate = useNavigate();

    useEffect(() => {
        const fetchProvider = async () => {
            setLoading(true);
            setError("");
            try {
                const res = await Apis.get(`${endpoints["provider-by-id"]}/${providerId}`);
                setProvider(res.data);

                const serviceRes = await Apis.get(`${endpoints["service-post-list"]}?providerId=${providerId}`);
                setServices(serviceRes.data.data || []);
            } catch (err) {
                setError("Không lấy được dữ liệu nhà cung cấp!");
                setProvider(null);
                setServices([]);
            }
            setLoading(false);
        };
        fetchProvider();
    }, [providerId]);

    // Callback nhận avgRating từ ProviderRating
    const handleAvgRatingChange = (avg, count) => {
        setAvgRating(avg);
        setRatingCount(count);
    };

    if (loading) return <Spinner animation="border" variant="primary" />;
    if (error) return <Alert variant="danger">{error}</Alert>;
    if (!provider) return <Alert variant="info">Không tìm thấy nhà cung cấp!</Alert>;

    return (
        <Container style={{ maxWidth: "950px", margin: "2rem auto" }}>
            <Card className="mb-4 p-3" style={{ background: "#f8f9fa", borderRadius: "1rem", boxShadow: "0 2px 8px #eee" }}>
                <Card.Body className="d-flex flex-column align-items-center">
                    {provider.avatar && (
                        <img
                            src={provider.avatar}
                            alt="Avatar"
                            style={{ width: 110, height: 110, borderRadius: "50%", marginBottom: "1rem", border: "3px solid #0d6efd", objectFit: "cover" }}
                        />
                    )}
                    <Card.Title style={{ fontSize: "2.2rem", fontWeight: "bold", color: "#0d6efd", marginBottom: "0.2rem" }}>
                        {provider.companyName}
                    </Card.Title>
                    {/* Hiển thị trung bình sao ngay dưới tên công ty */}
                    {avgRating && (
                        <div style={{ fontSize: "1.25rem", color: "#FFD700", fontWeight: 600, marginBottom: "0.7rem" }}>
                            ★ {avgRating} / 5 {ratingCount > 0 && <span style={{ color: "#444", fontWeight: 400 }}>({ratingCount} đánh giá)</span>}
                        </div>
                    )}
                    <Table borderless size="sm" style={{ maxWidth: 400 }}>
                        <tbody>
                            <tr>
                                <td style={{ fontWeight: "bold" }}>Địa chỉ:</td>
                                <td>{provider.address || "Chưa cập nhật"}</td>
                            </tr>
                            <tr>
                                <td style={{ fontWeight: "bold" }}>Email:</td>
                                <td>{provider.email || "Chưa cập nhật"}</td>
                            </tr>
                            <tr>
                                <td style={{ fontWeight: "bold" }}>Điện thoại:</td>
                                <td>{provider.phoneNumber || "Chưa cập nhật"}</td>
                            </tr>
                            <tr>
                                <td style={{ fontWeight: "bold" }}>Trạng thái:</td>
                                <td>{provider.state || "Đang cập nhật"}</td>
                            </tr>
                        </tbody>
                    </Table>
                </Card.Body>
            </Card>

            <h3 style={{ fontWeight: "bold", marginBottom: "1.2rem", color: "#0d6efd" }}>Các dịch vụ của nhà cung cấp</h3>
            <Row xs={1} sm={2} md={3} lg={4} className="g-4 mt-2">
                {services.map(s => (
                    <Col key={s.id}>
                        <Card
                            className="shadow-sm h-100"
                            style={{ cursor: "pointer", borderRadius: "0.7rem", transition: "transform 0.2s", border: "1.5px solid #eee" }}
                            onClick={() => navigate(`/service-post/${s.id}`)}
                            onMouseOver={e => e.currentTarget.style.transform = "scale(1.03)"}
                            onMouseOut={e => e.currentTarget.style.transform = "scale(1)"}
                        >
                            {s.image && <Card.Img variant="top" src={s.image} style={{ objectFit: "cover", height: "140px", borderRadius: "0.7rem 0.7rem 0 0" }} />}
                            <Card.Body>
                                <Card.Title style={{ fontWeight: "bold", fontSize: "1.13rem", color: "#0d6efd" }}>{s.name}</Card.Title>
                                <Card.Text>
                                    <span style={{ color: "#28a745", fontWeight: "bold", fontSize: "1.08rem" }}>
                                        {s.price?.toLocaleString()} VNĐ
                                    </span>
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>
            {services.length === 0 && <Alert variant="info" className="mt-3">Nhà cung cấp chưa có dịch vụ nào.</Alert>}


            <ProviderRating providerId={providerId} currentUser={currentUser} onAvgRatingChange={handleAvgRatingChange} />
        </Container>
    );
};

export default ProviderDetail;