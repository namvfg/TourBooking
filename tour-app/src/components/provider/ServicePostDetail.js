import React, { useEffect, useState, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios, { endpoints } from "../configs/Apis";
import { MyUserContext } from "../configs/Context";
import { Container, Row, Col, Card, Spinner, Alert } from "react-bootstrap";
import parse from "html-react-parser";

const formatDate = (dateVal) => {
    if (!dateVal) return "";
    if (typeof dateVal === "number") {
        const date = new Date(dateVal);
        return isNaN(date.getTime()) ? "" : date.toLocaleString("vi-VN");
    }
    if (typeof dateVal === "string" && dateVal.match(/^\d{4}-\d{2}-\d{2}$/)) {
        dateVal = dateVal + "T00:00:00";
    }
    const date = new Date(dateVal);
    return isNaN(date.getTime()) ? "" : date.toLocaleString("vi-VN");
};

const ServicePostDetail = () => {
    const { id } = useParams();
    const [user] = useContext(MyUserContext);
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const loadDetail = async () => {
            setLoading(true);
            setError("");
            try {
                const url = `${endpoints["service-post-detail"]}/${id}`;
                const res = await axios.get(url);
                setPost(res.data);
            } catch (err) {
                setError("Không lấy được thông tin dịch vụ!");
                setPost(null);
            }
            setLoading(false);
        };
        loadDetail();
    }, [id, user]);

    if (loading) return <Spinner animation="border" variant="primary" />;
    if (error) return <Alert variant="danger">{error}</Alert>;
    if (!post) return <Alert variant="info">Không tìm thấy dịch vụ!</Alert>;

    return (
        <Container className="my-5">
            <Card className="shadow overflow-hidden">
                <Row className="g-0">
                    {/* Mobile: ảnh trên */}
                    {post.image && (
                        <Col xs={12} className="d-block d-md-none">
                            <img
                                src={post.image}
                                alt={post.name}
                                className="img-fluid w-100"
                                style={{ height: "250px", objectFit: "cover" }}
                            />
                        </Col>
                    )}

                    {/* Desktop: ảnh bên trái */}
                    {post.image && (
                        <Col md={5} className="d-none d-md-block">
                            <img
                                src={post.image}
                                alt={post.name}
                                className="img-fluid h-100 w-100"
                                style={{ objectFit: "cover" }}
                            />
                        </Col>
                    )}

                    {/* Nội dung */}
                    <Col md={post.image ? 7 : 12}>
                        <div style={{ padding: "2rem", maxHeight: "500px", overflowY: "auto" }}>
                            <h4 className="text-primary fw-bold mb-2">{post.companyName}</h4>
                            <h2 className="fw-bold mb-3">{post.name}</h2>

                            <div className="text-success fs-5 fw-bold mb-3">
                                {post.price ? Number(post.price).toLocaleString("vi-VN") : 0} VNĐ
                            </div>

                            <p><b>Địa chỉ:</b> {post.address || ""}</p>
                            <p><b>Loại:</b> {post.serviceType || ""}</p>
                            <p><b>Ngày tạo:</b> {formatDate(post.createdDate)}</p>
                            <p><b>Còn lại:</b> {post.availableSlot ?? 0} slot</p>

                            {post.serviceType === "ROOM" && (
                                <>
                                    <p><b>Ngày bắt đầu phòng:</b> {formatDate(post.roomStartDate)}</p>
                                    <p><b>Ngày kết thúc phòng:</b> {formatDate(post.roomEndDate)}</p>
                                </>
                            )}

                            {post.serviceType === "TOUR" && (
                                <>
                                    <p><b>Ngày bắt đầu tour:</b> {formatDate(post.tourStartDate)}</p>
                                    <p><b>Ngày kết thúc tour:</b> {formatDate(post.tourEndDate)}</p>
                                </>
                            )}

                            {post.serviceType === "TRANSPORTATION" && (
                                <>
                                    <p><b>Loại phương tiện:</b> {post.transportType}</p>
                                    <p><b>Ngày khởi hành:</b> {formatDate(post.transportStartDate)}</p>
                                    <p><b>Điểm đến:</b> {post.destination}</p>
                                </>
                            )}

                            <div className="mt-3">
                                <b>Mô tả:</b>
                                <div className="service-desc-content mt-2">
                                    {post.description ? parse(post.description) : null}
                                </div>
                            </div>

                            <div className="mt-4 text-end">
                                <button
                                    className="btn btn-primary"
                                    onClick={() => navigate(`/payment?postId=${post.id}`)}
                                >
                                    Đặt vé
                                </button>
                            </div>
                        </div>
                    </Col>
                </Row>
            </Card>
        </Container>
    );
};

export default ServicePostDetail;
