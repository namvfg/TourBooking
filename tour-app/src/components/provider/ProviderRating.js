import React, { useEffect, useState } from "react";
import { endpoints } from "../configs/Apis";
import { Card, Button, Alert, Form, Row, Col } from "react-bootstrap";
import cookies from "react-cookies";
import Apis from "../configs/Apis";
import { useNavigate } from "react-router-dom";

const StarSelect = ({ value, onChange }) => (
    <div style={{
        fontSize: "2.2rem",
        marginBottom: 10,
        letterSpacing: "4px",
        userSelect: "none",
        textAlign: "center"
    }}>
        {[1, 2, 3, 4, 5].map(i => (
            <span
                key={i}
                style={{
                    cursor: "pointer",
                    color: i <= value ? "#FFD700" : "#e4e5e9",
                    marginRight: 6,
                    transition: "color .2s",
                    filter: i <= value ? "drop-shadow(0 2px 2px #ffe085)" : "none"
                }}
                onClick={() => onChange(i)}
                onMouseOver={e => e.currentTarget.style.color = "#FFC107"}
                onMouseOut={e => e.currentTarget.style.color = i <= value ? "#FFD700" : "#e4e5e9"}
            >★</span>
        ))}
    </div>
);

const ProviderRating = ({ providerId, currentUser, onAvgRatingChange }) => {
    const [ratings, setRatings] = useState([]);
    const [myRating, setMyRating] = useState(null);
    const [rate, setRate] = useState(5);
    const [comment, setComment] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const navigate = useNavigate();

    // Tính trung bình rating
    const avgRating = ratings.length > 0
        ? (ratings.reduce((acc, cur) => acc + cur.rate, 0) / ratings.length).toFixed(1)
        : null;

    // Gửi avgRating về ProviderDetail mỗi khi ratings thay đổi
    useEffect(() => {
        if (onAvgRatingChange) onAvgRatingChange(avgRating, ratings.length);
    }, [avgRating, ratings.length, onAvgRatingChange]);

    useEffect(() => {
        const fetch = async () => {
            try {
                const token = cookies.load("token");
                const res = await Apis.get(
                    `${endpoints["provider-ratings"]}/${providerId}/rating`,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                setRatings(Array.isArray(res.data) ? res.data : []);
                if (currentUser) {
                    const mine = (Array.isArray(res.data) ? res.data : []).find(r => r.userId === currentUser.id);
                    if (mine) {
                        setMyRating(mine);
                        setRate(mine.rate);
                        setComment(mine.comment);
                    } else {
                        setMyRating(null);
                        setRate(5);
                        setComment("");
                    }
                }
            } catch (err) { setRatings([]); setError("Không lấy được đánh giá!"); }
        };
        fetch();
    }, [providerId, currentUser]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(""); setSuccess("");
        try {
            const token = cookies.load("token");
            await Apis.post(
                `${endpoints["provider-ratings"]}/${providerId}/rating`,
                { rate, comment },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setSuccess("Đã gửi đánh giá!");
            const res = await Apis.get(
                `${endpoints["provider-ratings"]}/${providerId}/rating`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setRatings(Array.isArray(res.data) ? res.data : []);
            const mine = (Array.isArray(res.data) ? res.data : []).find(r => r.userId === currentUser.id);
            setMyRating(mine);
        } catch (err) { setError("Gửi đánh giá thất bại!"); }
    };

    return (
        <div style={{
            maxWidth: 950,
            margin: "32px auto 24px auto",
            display: "flex",
            flexDirection: "column",
            alignItems: "center"
        }}>
            <Card
                className="shadow-sm"
                style={{
                    borderRadius: "1.1rem",
                    border: "1.5px solid #eef",
                    background: "#fff",
                    padding: "16px 0",
                    width: "100%",
                    minHeight: "auto",
                    boxSizing: "border-box"
                }}
            >
                <Card.Body>
                    <h5 style={{
                        fontWeight: "bold",
                        color: "#0d6efd",
                        textAlign: "center",
                        fontSize: "1.18rem",
                        marginBottom: "16px"
                    }}>
                        Đánh giá & bình luận của người dùng
                    </h5>

                    {/* Hiển thị trung bình sao phía trên danh sách đánh giá */}
                    {avgRating && (
                        <div style={{ fontSize: "1.25rem", color: "#FFD700", fontWeight: 600, marginBottom: 12, textAlign: "center" }}>
                            ★ {avgRating} / 5 ({ratings.length} đánh giá)
                        </div>
                    )}

                    {Array.isArray(ratings) && ratings.length > 0 &&
                        <Row className="mb-2 gx-2 gy-2">
                            {ratings.map(r => (
                                <Col xs={12} key={r.id}>
                                    <Card className="mb-2"
                                        style={{
                                            borderRadius: "0.7rem",
                                            background: "#f8f9fa",
                                            boxShadow: "0 1px 7px #eee",
                                            border: "none",
                                            minHeight: "56px"
                                        }}>
                                        <Card.Body style={{ padding: "9px 12px 7px 12px" }}>
                                            <div style={{
                                                display: "flex",
                                                alignItems: "center",
                                                marginBottom: 4
                                            }}>
                                                <img src={r.userAvatar} alt="avatar"
                                                    style={{
                                                        width: 24, height: 24,
                                                        borderRadius: "50%",
                                                        marginRight: 8,
                                                        border: "2px solid #eee"
                                                    }} />
                                                <b style={{ marginRight: 8 }}>{r.userName}</b>
                                                <span style={{ color: "#FFD700", fontSize: "1.08rem" }}>
                                                    {Array(r.rate).fill("★").join("")}
                                                </span>
                                                <span style={{ color: "#888", fontSize: "0.92em", marginLeft: 7 }}>({r.rate}/5)</span>
                                            </div>
                                            <div style={{ fontStyle: "italic", color: "#333", marginBottom: 2, fontSize: "0.98rem" }}>{r.comment}</div>
                                            <div style={{ fontSize: "0.93em", color: "#777" }}>{new Date(r.createdDate).toLocaleString()}</div>
                                        </Card.Body>
                                    </Card>
                                </Col>
                            ))}
                        </Row>
                    }

                    {currentUser &&
                        <Form className="mt-2" onSubmit={handleSubmit}>
                            <Form.Group>
                                <Form.Label style={{ fontWeight: "bold", color: "#0d6efd" }}>Chọn số sao:</Form.Label>
                                <StarSelect value={rate} onChange={setRate} />
                            </Form.Group>
                            <Form.Group className="mt-1">
                                <Form.Label style={{ fontWeight: "bold", color: "#0d6efd" }}>Bình luận:</Form.Label>
                                <Form.Control as="textarea" rows={2} value={comment} onChange={e => setComment(e.target.value)} required
                                    style={{ resize: "vertical", borderRadius: "0.6rem", fontSize: "0.97rem" }} />
                            </Form.Group>
                            <div className="d-flex justify-content-center">
                                <Button type="submit" className="mt-2 px-4" variant="primary" style={{
                                    borderRadius: "0.5rem",
                                    fontWeight: 600,
                                    fontSize: "1.06rem"
                                }}>
                                    {myRating ? "Sửa đánh giá" : "Gửi đánh giá"}
                                </Button>
                            </div>
                            {error && <Alert variant="danger" className="mt-2">{error}</Alert>}
                            {success && <Alert variant="success" className="mt-2">{success}</Alert>}
                        </Form>
                    }
                    {!currentUser && (
                        <Alert variant="warning" className="mt-2 text-center">
                            Bạn cần{" "}
                            <span
                                style={{ color: "#0d6efd", textDecoration: "underline", cursor: "pointer", fontWeight: 600 }}
                                onClick={() => navigate("/login")}
                            >
                                đăng nhập
                            </span>
                            {" "}để đánh giá nhà cung cấp.
                        </Alert>
                    )}
                </Card.Body>
            </Card>
        </div>
    );
};

export default ProviderRating;