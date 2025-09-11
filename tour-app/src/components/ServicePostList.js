import React, { useEffect, useState, useContext } from "react";
import axios, { endpoints } from "./configs/Apis";
import { Card, Row, Col, Spinner, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { MyUserContext } from "./configs/Context";

const ServicePostList = ({ reloadCount = 0 }) => {
    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(8);
    const [totalPages, setTotalPages] = useState(1);
    const [total, setTotal] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const loadPosts = async (p = 0) => {
        setLoading(true);
        setError("");
        try {
            const url = `${endpoints["service-post-list"]}?page=${p}&size=${size}`;
            const res = await axios.get(url);

            let apiData = res.data;
            if (typeof apiData === "string") {
                try {
                    apiData = JSON.parse(apiData);
                } catch (e) {
                    setError("API trả về không phải JSON hợp lệ!");
                    setPosts([]);
                    setPage(0);
                    setSize(8);
                    setTotalPages(1);
                    setTotal(0);
                    setLoading(false);
                    return;
                }
            }

            if (!apiData || !Array.isArray(apiData.data)) {
                setError("Dữ liệu trả về không hợp lệ!");
                setPosts([]);
                setPage(0);
                setSize(8);
                setTotalPages(1);
                setTotal(0);
                setLoading(false);
                return;
            }

            const postList = apiData.data;
            setPosts(postList);
            setPage(apiData.page ?? 0);
            setSize(apiData.size ?? 8);
            setTotalPages(apiData.totalPages ?? 1);
            setTotal(apiData.total ?? 0);
        } catch (err) {
            setError("Không lấy được danh sách dịch vụ! " + (err?.message || ""));
            setPosts([]);
            setPage(0);
            setSize(8);
            setTotalPages(1);
            setTotal(0);
        }
        setLoading(false);
    };

    useEffect(() => {
        loadPosts(page);
        // eslint-disable-next-line
    }, [page, size, reloadCount]);

    const handlePrev = () => { if (page > 0) setPage(page - 1); };
    const handleNext = () => { if (page < totalPages - 1) setPage(page + 1); };

    return (
        <div>
            <h2 className="mb-4" style={{ fontWeight: "bold" }}>
                🛎️ Danh sách dịch vụ <span style={{ color: "#007bff" }}>({total} dịch vụ)</span>
            </h2>
            {error && <Alert variant="danger" className="mt-2">{error}</Alert>}
            {loading ? (
                <div className="text-center my-5">
                    <Spinner animation="border" variant="primary" />
                </div>
            ) : (
                posts.length > 0 ? (
                    <Row xs={1} sm={2} md={3} lg={4} className="g-4">
                        {posts.map(p => (
                            <Col key={p.id}>
                                <Card
                                    className="shadow-sm h-100"
                                    style={{ cursor: "pointer" }}
                                    onClick={() => navigate(`/service-post/${p.id}`)}
                                >
                                    {p.image && <Card.Img variant="top" src={p.image} style={{ objectFit: "cover", height: "160px" }} />}
                                    <Card.Body>
                                        {/* Tên nhà cung cấp nổi bật */}
                                        <Card.Title style={{ color: "#0d6efd", fontWeight: "bold", fontSize: "1.2rem" }}>
                                            {p.companyName}
                                        </Card.Title>
                                        {/* Tên dịch vụ nổi bật dưới */}
                                        <Card.Title style={{ fontWeight: "bold", fontSize: "1.5rem" }}>
                                            {p.name}
                                        </Card.Title>
                                        <Card.Text>
                                            <span style={{ color: "#28a745", fontWeight: "bold" }}>
                                                {p.price?.toLocaleString()} VNĐ
                                            </span><br />
                                            <span><b>Địa chỉ:</b> {p.address}</span><br />
                                            <span><b>Loại:</b> {p.serviceType}</span>
                                        </Card.Text>
                                    </Card.Body>
                                    <Card.Footer>
                                        <small className="text-muted">Còn lại: {p.availableSlot} slot</small>
                                    </Card.Footer>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                ) : (
                    <Alert variant="info" className="mt-4">Không có dịch vụ nào.</Alert>
                )
            )}
            <div className="d-flex justify-content-center align-items-center my-4 gap-2">
                <button className="btn btn-outline-primary" disabled={page === 0} onClick={handlePrev}>Trước</button>
                <span><b>Trang {page + 1}/{totalPages}</b></span>
                <button className="btn btn-outline-primary" disabled={page >= totalPages - 1} onClick={handleNext}>Sau</button>
            </div>
        </div>
    );
};

export default ServicePostList;