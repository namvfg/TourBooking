import React, { useEffect, useState } from "react";
import axios, { endpoints } from "./configs/Apis";
import { Card, Row, Col, Spinner, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const ServicePostList = ({ reloadCount = 0 }) => {
    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(8);
    const [totalPages, setTotalPages] = useState(1);
    const [total, setTotal] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const navigate = useNavigate();

  
    const [keyword, setKeyword] = useState("");
    const [serviceType, setServiceType] = useState("");
    const [minPrice, setMinPrice] = useState("");
    const [maxPrice, setMaxPrice] = useState("");

    
    const loadPosts = async (p = 0) => {
        setLoading(true);
        setError("");
        try {
            let params = [];
            if (keyword) params.push(`keyword=${encodeURIComponent(keyword)}`);
            if (serviceType) params.push(`serviceType=${encodeURIComponent(serviceType)}`);
            if (minPrice) params.push(`minPrice=${minPrice}`);
            if (maxPrice) params.push(`maxPrice=${maxPrice}`);
            params.push(`page=${p}`);
            params.push(`size=${size}`);
            const url = `${endpoints["service-post-search"]}?${params.join("&")}`;

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

            setPosts(apiData.data);
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
    }, [page, size, reloadCount, keyword, serviceType, minPrice, maxPrice]);

    const handlePrev = () => { if (page > 0) setPage(page - 1); };
    const handleNext = () => { if (page < totalPages - 1) setPage(page + 1); };

    const handleSearch = () => {
        setPage(0); 
        loadPosts(0);
    };

    return (
        <div>
            <div className="d-flex gap-2 my-3 flex-wrap">
                <input
                    type="text"
                    className="form-control"
                    placeholder="Tìm tên dịch vụ..."
                    value={keyword}
                    onChange={e => setKeyword(e.target.value)}
                    style={{ maxWidth: "220px" }}
                />
                <select
                    className="form-select"
                    value={serviceType}
                    onChange={e => setServiceType(e.target.value)}
                    style={{ maxWidth: "140px" }}
                >
                    <option value="">Tất cả loại</option>
                    <option value="ROOM">Phòng</option>
                    <option value="TOUR">Tour</option>
                    <option value="TRANSPORTATION">Phương tiện</option>
                </select>
                <input
                    type="number"
                    className="form-control"
                    placeholder="Giá tối thiểu"
                    value={minPrice}
                    onChange={e => setMinPrice(e.target.value)}
                    style={{ maxWidth: "150px" }}
                />
                <input
                    type="number"
                    className="form-control"
                    placeholder="Giá tối đa"
                    value={maxPrice}
                    onChange={e => setMaxPrice(e.target.value)}
                    style={{ maxWidth: "150px" }}
                />
                <button className="btn btn-primary" onClick={handleSearch}>
                    Tìm kiếm
                </button>
            </div>
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
                                        <Card.Title style={{ color: "#0d6efd", fontWeight: "bold", fontSize: "1.2rem" }}>
                                            {p.companyName}
                                        </Card.Title>
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