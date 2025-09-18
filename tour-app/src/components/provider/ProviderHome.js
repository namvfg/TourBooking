import { useContext, useEffect, useState } from "react";
import { MyUserContext } from "../configs/Context";
import { authApis, endpoints } from "../configs/Apis";
import { Card, Button, Spinner, Row, Col } from "react-bootstrap";
import { toast } from "react-toastify";
import cookie from "react-cookies";
import { useNavigate } from "react-router-dom";

const ProviderHome = () => {
    const [user] = useContext(MyUserContext);
    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const fetchPosts = async (pageNumber = 0) => {
        try {
            setLoading(true);
            const token = cookie.load("token");

            const res = await authApis(token).get(endpoints["service-post-list"], {
                params: {
                    page: pageNumber,
                    size: 6,
                    providerId: user?.provider?.providerId,
                },
            });

            setPosts(res.data.data || []);
            setPage(res.data.page || 0);
            setTotalPages(res.data.totalPages || 1);
        } catch (err) {
            console.error("Lỗi tải bài đăng:", err);
            toast.error("Không thể tải danh sách bài đăng!");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (user?.provider?.providerId) fetchPosts();
    }, [user]);

    const handlePrev = () => {
        if (page > 0) fetchPosts(page - 1);
    };

    const handleNext = () => {
        if (page < totalPages - 1) fetchPosts(page + 1);
    };

    return (
        <>
            <h3 className="mb-4 text-center">Bài đăng của bạn</h3>
            {user && user.role === "PROVIDER" && (
                <Button variant="primary" className="mb-3" onClick={() => navigate("/add-service-post")}>
                    Thêm dịch vụ
                </Button>
            )}
            {loading ? (
                <div className="text-center">
                    <Spinner animation="border" />
                </div>
            ) : posts.length === 0 ? (
                <p className="text-muted text-center">Bạn chưa có bài đăng nào.</p>
            ) : (
                <Row xs={1} md={2} lg={3} className="g-4">
                    {posts.map((p) => (
                        <Col key={p.id}>
                            <Card
                                className="h-100 shadow-sm"
                                style={{ cursor: "pointer" }}
                                onClick={() => navigate(`/service-post/${p.id}`)}
                            >
                                <Card.Img
                                    variant="top"
                                    src={p.image}
                                    alt={p.name}
                                    style={{ height: "180px", objectFit: "cover" }}
                                />
                                <Card.Body>
                                    <Card.Title>{p.name}</Card.Title>
                                    <Card.Text>
                                        <strong>Giá:</strong> {p.price?.toLocaleString()} VNĐ
                                        <br />
                                        <strong>Địa chỉ:</strong> {p.address}
                                        <br />
                                        <strong>Slot còn lại:</strong> {p.availableSlot}
                                        <br />
                                        {p.serviceType && (
                                            <>
                                                <strong>Loại dịch vụ:</strong> {p.serviceType}
                                            </>
                                        )}
                                    </Card.Text>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            )}

            {totalPages > 1 && (
                <div className="mt-4 d-flex justify-content-center align-items-center gap-3">
                    <Button variant="outline-primary" onClick={handlePrev} disabled={page === 0}>
                        ← Trước
                    </Button>
                    <span>
                        Trang <strong>{page + 1}</strong> / {totalPages}
                    </span>
                    <Button
                        variant="outline-primary"
                        onClick={handleNext}
                        disabled={page === totalPages - 1}
                    >
                        Tiếp →
                    </Button>
                </div>
            )}
        </>
    );
};

export default ProviderHome;
