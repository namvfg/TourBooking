import React, { useEffect, useState, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container, Row, Col, Card, Spinner, Alert, Button, Table } from "react-bootstrap";
import parse from "html-react-parser";
import { MyUserContext } from "../configs/Context";
import { toast } from "react-toastify";
import cookie from "react-cookies";
import Apis, { endpoints, authApis } from "../configs/Apis";
import EditServicePostModal from "./EditServicePostModal";
import cookies from 'react-cookies';

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
    const [showEditModal, setShowEditModal] = useState(false);
    const [bookings, setBookings] = useState([]);
    const [loadingBookings, setLoadingBookings] = useState(false);
    const [errorBookings, setErrorBookings] = useState("");
    const navigate = useNavigate();

    const isOwner =
        user &&
        user.role === "PROVIDER" &&
        user.provider &&
        post &&
        Number(user.provider.providerId) === Number(post.serviceProviderId);

    const loadDetail = async () => {
        setLoading(true);
        setError("");
        try {
            const url = `${endpoints["service-post-detail"]}/${id}`;
            const res = await Apis.get(url);
            setPost(res.data);
        } catch (err) {
            setError("Không lấy được thông tin dịch vụ!");
            setPost(null);
        }
        setLoading(false);
    };

    const loadBookings = async () => {
        if (!isOwner) return;

        setLoadingBookings(true);
        setErrorBookings("");
        try {
            const token = cookies.load("token");
            const res = await authApis(token).get(endpoints["provider-transactions"](id));
            setBookings(res.data);
        } catch (err) {
            setErrorBookings("Không thể tải danh sách người đặt dịch vụ!");
        }
        setLoadingBookings(false);
    };

    useEffect(() => {
        loadDetail();
    }, [id, user]);

    useEffect(() => {
        if (isOwner) {
            loadBookings();
        }

    }, [post]);

    const handleDelete = async () => {
        if (!window.confirm("Bạn chắc chắn muốn xóa dịch vụ này?")) return;
        try {
            const token = cookie.load("token");
            await authApis(token).delete(`${endpoints["service-post-delete"]}/${id}`);
            toast.success("Xóa dịch vụ thành công!");
            navigate("/service-posts");
        } catch (err) {
            if (err?.response?.status === 401) {
                toast.error("Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!");
                navigate("/login");
            } else {
                toast.error("Lỗi xóa dịch vụ! " + (err?.response?.data || err.message));
            }
        }
    };

    if (loading) return <Spinner animation="border" variant="primary" />;
    if (error) return <Alert variant="danger">{error}</Alert>;
    if (!post) return <Alert variant="info">Không tìm thấy dịch vụ!</Alert>;

    return (
        <Container className="my-5">
            <Card className="shadow overflow-hidden mb-4">
                <Row className="g-0">
                    {post.image && (
                        <Col xs={12} md={5}>
                            <img
                                src={post.image}
                                alt={post.name}
                                className="img-fluid w-100 h-100"
                                style={{ height: "250px", objectFit: "cover" }}
                            />
                        </Col>
                    )}
                    <Col md={post.image ? 7 : 12}>
                        <div style={{ padding: "2rem", maxHeight: "700px", overflowY: "auto" }}>
                            <h4
                                className="text-primary fw-bold mb-2"
                                style={{ cursor: "pointer", textDecoration: "underline" }}
                                onClick={() => navigate(`/provider/${post.serviceProviderId}`)}
                            >
                                {post.companyName}
                            </h4>
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

                            {isOwner && (
                                <div className="mt-4 d-flex justify-content-end gap-2">
                                    <Button variant="warning" onClick={() => setShowEditModal(true)}>
                                        Sửa
                                    </Button>
                                    <Button variant="danger" onClick={handleDelete}>
                                        Xóa
                                    </Button>
                                </div>
                            )}

                            {user?.role === "USER" && (
                                <div className="mt-4 d-flex justify-content-end gap-2">
                                    <Button
                                        variant="primary"
                                        onClick={() => navigate(`/payment?postId=${post.id}`)}
                                    >
                                        Đặt vé
                                    </Button>

                                    <Button
                                        variant="outline-primary"
                                        onClick={() => navigate(`/compare/${post.id}`)}
                                    >
                                        So sánh
                                    </Button>
                                </div>
                            )}
                        </div>
                    </Col>
                </Row>
            </Card>

            {isOwner && (
                <Card className="shadow mb-4">
                    <Card.Header className="fw-bold">Danh sách người đã đặt</Card.Header>
                    <Card.Body style={{ maxHeight: "300px", overflowY: "auto" }}>
                        {loadingBookings ? (
                            <Spinner animation="border" variant="primary" />
                        ) : errorBookings ? (
                            <Alert variant="danger">{errorBookings}</Alert>
                        ) : bookings.length === 0 ? (
                            <Alert variant="info">Chưa có ai đặt dịch vụ này.</Alert>
                        ) : (
                            <Table bordered hover responsive>
                                <thead>
                                    <tr>
                                        <th>Họ tên</th>
                                        <th>Email</th>
                                        <th>Số slot</th>
                                        <th>Tổng tiền</th>
                                        <th>Ngày đặt</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {bookings.map((b, idx) => (
                                        <tr key={idx}>
                                            <td>{b.fullName}</td>
                                            <td>{b.email}</td>
                                            <td>{b.slotQuantity}</td>
                                            <td>{Number(b.totalAmount).toLocaleString("vi-VN")} VNĐ</td>
                                            <td>{formatDate(b.createdDate)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        )}
                    </Card.Body>
                </Card>
            )}

            <EditServicePostModal
                show={showEditModal}
                onHide={() => setShowEditModal(false)}
                post={post}
                onUpdated={loadDetail}
            />
        </Container>
    );
};

export default ServicePostDetail;
