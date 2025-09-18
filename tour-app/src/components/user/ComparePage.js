import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Card, Col, Container, Row, Spinner, Button, Table } from "react-bootstrap";
import Apis, { endpoints } from "../configs/Apis";

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

const getStartDate = (post) => {
    if (!post) return null;
    switch (post.serviceType) {
        case "ROOM": return post.roomStartDate;
        case "TOUR": return post.tourStartDate;
        case "TRANSPORTATION": return post.transportStartDate;
        default: return null;
    }
};

const getEndDate = (post) => {
    if (!post) return null;
    switch (post.serviceType) {
        case "ROOM": return post.roomEndDate;
        case "TOUR": return post.tourEndDate;
        default: return null;
    }
};

const renderComparison = (label, val1, val2, unit = "", isNumber = false, isDate = false) => {
    const parseVal = (val) => {
        if (isDate) return val ? new Date(val).getTime() : null;
        return isNumber ? Number(val || 0) : (val || "").toString().trim().toLowerCase();
    };
    const v1 = parseVal(val1);
    const v2 = parseVal(val2);

    const getStyle = (v, other) => {
        if (v == null || other == null) return {};
        if (v < other) return { color: "green", fontWeight: "bold" };
        if (v > other) return { color: "red", fontWeight: "bold" };
        return { color: "gray" };
    };

    const formatVal = (v) => {
        if (v == null) return "—";
        if (isDate) return new Date(v).toLocaleDateString("vi-VN");
        if (isNumber) return Number(v).toLocaleString("vi-VN") + (unit ? ` ${unit}` : "");
        return v;
    };

    return (
        <tr key={label}>
            <td>{label}</td>
            <td style={getStyle(v1, v2)}>{formatVal(v1)}</td>
            <td style={getStyle(v2, v1)}>{formatVal(v2)}</td>
        </tr>
    );
};

const CompareItem = ({ post, onReset }) => {
    if (!post) return null;

    return (
        <Card className="mb-3 shadow-sm">
            <Card.Header className="d-flex justify-content-between align-items-center">
                <strong>{post.name}</strong>
                <Button variant="outline-danger" size="sm" onClick={onReset}>Chọn lại</Button>
            </Card.Header>
            <Card.Body>
                <Row>
                    {post.image && (
                        <Col md={4}>
                            <img
                                src={post.image}
                                alt={post.name}
                                className="img-fluid rounded shadow-sm"
                                style={{ objectFit: "cover", width: "100%", height: "150px" }}
                            />
                        </Col>
                    )}
                    <Col md={post.image ? 8 : 12}>
                        <p><strong>Loại:</strong> {post.serviceType}</p>
                        <p><strong>Giá:</strong> {post.price?.toLocaleString()} VND</p>
                        <p><strong>Địa chỉ:</strong> {post.address}</p>
                        <p><strong>Ngày tạo:</strong> {formatDate(post.createdDate)}</p>
                        <p><strong>Số slot còn lại:</strong> {post.availableSlot}</p>
                        {post.serviceType === "ROOM" && (
                            <>
                                <p><strong>Ngày bắt đầu phòng:</strong> {formatDate(post.roomStartDate)}</p>
                                <p><strong>Ngày kết thúc phòng:</strong> {formatDate(post.roomEndDate)}</p>
                            </>
                        )}
                        {post.serviceType === "TOUR" && (
                            <>
                                <p><strong>Ngày bắt đầu tour:</strong> {formatDate(post.tourStartDate)}</p>
                                <p><strong>Ngày kết thúc tour:</strong> {formatDate(post.tourEndDate)}</p>
                            </>
                        )}
                        {post.serviceType === "TRANSPORTATION" && (
                            <>
                                <p><strong>Loại phương tiện:</strong> {post.transportType}</p>
                                <p><strong>Ngày khởi hành:</strong> {formatDate(post.transportStartDate)}</p>
                                <p><strong>Điểm đến:</strong> {post.destination}</p>
                            </>
                        )}
                    </Col>
                </Row>
            </Card.Body>
        </Card>
    );
};

const ComparePage = () => {
    const { leftId } = useParams();
    const [leftPost, setLeftPost] = useState(null);
    const [rightPost, setRightPost] = useState(null);
    const [rightList, setRightList] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadLeft = async () => {
            try {
                let res = await Apis.get(`${endpoints["service-post-detail"]}/${leftId}`);
                setLeftPost(res.data);
            } catch (err) {
                console.error("Lỗi load bên trái", err);
            }
        };
        loadLeft();
    }, [leftId]);

    useEffect(() => {
        const loadRightList = async () => {
            if (!leftPost?.serviceType) return;
            setLoading(true);
            try {
                let res = await Apis.get(endpoints["service-post-search"], {
                    params: {
                        serviceType: leftPost.serviceType,
                        page: 0,
                        size: 10
                    },
                });
                setRightList(res.data.data.filter(p => p.id !== leftPost.id));
            } catch (err) {
                console.error("Lỗi load danh sách bên phải", err);
            } finally {
                setLoading(false);
            }
        };
        loadRightList();
    }, [leftPost]);

    const chooseRight = async (id) => {
        try {
            let res = await Apis.get(`${endpoints["service-post-detail"]}/${id}`);
            setRightPost(res.data);
        } catch (err) {
            console.error("Lỗi chọn bên phải", err);
        }
    };

    return (
        <Container className="py-4">
            <h3 className="text-center mb-4">So sánh dịch vụ</h3>
            <Row>
                <Col md={6}>
                    <h5 className="text-primary">Dịch vụ gốc</h5>
                    <CompareItem post={leftPost} onReset={() => { }} />
                </Col>

                <Col md={6}>
                    <h5 className="text-success">Dịch vụ so sánh</h5>
                    {rightPost ? (
                        <CompareItem post={rightPost} onReset={() => setRightPost(null)} />
                    ) : loading ? (
                        <Spinner animation="border" />
                    ) : (
                        <div>
                            {rightList.length > 0 ? (
                                rightList.map((post) => (
                                    <Card
                                        key={post.id}
                                        className="mb-2 clickable"
                                        onClick={() => chooseRight(post.id)}
                                        style={{ cursor: "pointer" }}
                                    >
                                        <Card.Body>
                                            <strong>{post.name}</strong> - {post.price?.toLocaleString()} VND
                                        </Card.Body>
                                    </Card>
                                ))
                            ) : (
                                <p>Không tìm thấy dịch vụ nào cùng loại.</p>
                            )}
                        </div>
                    )}
                </Col>
            </Row>

            {leftPost && rightPost && (
                <Card className="mt-4 shadow-sm">
                    <Card.Header className="fw-bold">So sánh chi tiết</Card.Header>
                    <Card.Body>
                        <Table bordered responsive>
                            <thead>
                                <tr>
                                    <th>Tiêu chí</th>
                                    <th>Dịch vụ 1</th>
                                    <th>Dịch vụ 2</th>
                                </tr>
                            </thead>
                            <tbody>
                                {renderComparison("Giá", leftPost?.price, rightPost?.price, "VNĐ", true)}
                                {renderComparison("Số slot còn lại", leftPost?.availableSlot, rightPost?.availableSlot, "", true)}
                                {renderComparison("Ngày bắt đầu", getStartDate(leftPost), getStartDate(rightPost), "", false, true)}
                                {renderComparison("Ngày kết thúc", getEndDate(leftPost), getEndDate(rightPost), "", false, true)}
                            </tbody>
                        </Table>
                    </Card.Body>
                </Card>
            )}
        </Container>
    );
};

export default ComparePage;