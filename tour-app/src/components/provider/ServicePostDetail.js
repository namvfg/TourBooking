import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios, { endpoints, authApis } from "../configs/Apis";
import { MyUserContext } from "../configs/Context";
import { Card, Button, Spinner, Alert, Modal, Form } from "react-bootstrap";
import { toast } from "react-toastify";

// Hàm format lại ngày trả về từ BE (timestamp hoặc chuỗi)
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

    // Modal "Cập nhật"
    const [showEditModal, setShowEditModal] = useState(false);
    const [editData, setEditData] = useState(null);
    const [editLoading, setEditLoading] = useState(false);

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

    // Kiểm tra quyền owner: so sánh kiểu số để tránh lỗi kiểu dữ liệu
    const isOwner =
        user &&
        user.role === "PROVIDER" &&
        user.provider &&
        post &&
        Number(user.provider.providerId) === Number(post.serviceProviderId);

    // Mở modal cập nhật và truyền dữ liệu cũ vào
    const handleEdit = () => {
        if (!post) return;
        setEditData({
            name: post.name,
            description: post.description,
            price: post.price,
            availableSlot: post.availableSlot,
            address: post.address,
            serviceType: post.serviceType,
            image: null // Chưa hỗ trợ sửa ảnh, giữ nguyên ảnh cũ nếu không chọn
        });
        setShowEditModal(true);
    };

    // Xử lý submit cập nhật
    const handleEditSubmit = async (e) => {
        e.preventDefault();
        setEditLoading(true);
        try {
            const url = `${endpoints["service-post-edit"]}/${id}`;
            // Chuẩn bị dữ liệu gửi đi
            const formData = new FormData();
            formData.append("name", editData.name);
            formData.append("description", editData.description);
            formData.append("price", editData.price);
            formData.append("availableSlot", editData.availableSlot);
            formData.append("address", editData.address);
            formData.append("serviceType", editData.serviceType);
            if (editData.image) formData.append("image", editData.image);

            await authApis(user.token).put(url, formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });
            toast.success("Cập nhật thành công!");
            setShowEditModal(false);
            // Reload lại chi tiết
            const res = await axios.get(`${endpoints["service-post-detail"]}/${id}`);
            setPost(res.data);
        } catch (err) {
            toast.error("Lỗi cập nhật! " + (err?.response?.data || err.message));
        }
        setEditLoading(false);
    };

    const handleEditChange = (e) => {
        const { name, value, files } = e.target;
        setEditData((prev) => ({
            ...prev,
            [name]: files ? files[0] : value
        }));
    };

    const handleDelete = async () => {
        if (!window.confirm("Bạn chắc chắn muốn xóa dịch vụ này?")) return;
        try {
            await authApis(user.token).delete(`${endpoints["service-post-delete"]}/${id}`);
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
        <Card className="shadow my-4 mx-auto" style={{ maxWidth: "600px" }}>
            {post.image && (
                <Card.Img
                    variant="top"
                    src={post.image}
                    style={{ objectFit: "cover", height: "220px" }}
                />
            )}
            <Card.Body>
                {/* Tên nhà cung cấp nổi bật */}
                <Card.Title style={{ color: "#0d6efd", fontWeight: "bold", fontSize: "1.2rem" }}>
                    {post.companyName}
                </Card.Title>
                {/* Tên dịch vụ nổi bật dưới */}
                <Card.Title style={{ fontWeight: "bold", fontSize: "1.5rem" }}>
                    {post.name}
                </Card.Title>
                <Card.Text>
                    <span
                        style={{
                            color: "#28a745",
                            fontWeight: "bold",
                            fontSize: "1.2rem",
                        }}
                    >
                        {post.price ? Number(post.price).toLocaleString("vi-VN") : 0} VNĐ
                    </span>
                    <br />
                    <span>
                        <b>Địa chỉ:</b> {post.address || ""}
                    </span>
                    <br />
                    <span>
                        <b>Loại:</b> {post.serviceType || ""}
                    </span>
                    <br />
                    <span>
                        <b>Mô tả:</b> {post.description || ""}
                    </span>
                    <br />
                    <span>
                        <b>Ngày tạo:</b> {formatDate(post.createdDate)}
                    </span>
                    <br />
                    <span>
                        <b>Còn lại:</b> {post.availableSlot ?? 0} slot
                    </span>
                </Card.Text>
                {isOwner && (
                    <div className="d-flex gap-2 mt-3">
                        <Button variant="warning" onClick={handleEdit}>
                            Cập nhật
                        </Button>
                        <Button variant="danger" onClick={handleDelete}>
                            Xóa
                        </Button>
                    </div>
                )}
            </Card.Body>

            {/* Modal cập nhật dịch vụ */}
            <Modal show={showEditModal} onHide={() => setShowEditModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Cập nhật dịch vụ</Modal.Title>
                </Modal.Header>
                <Form onSubmit={handleEditSubmit}>
                    <Modal.Body>
                        <Form.Group className="mb-3">
                            <Form.Label>Tên dịch vụ</Form.Label>
                            <Form.Control
                                type="text"
                                name="name"
                                value={editData?.name || ""}
                                onChange={handleEditChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Mô tả</Form.Label>
                            <Form.Control
                                as="textarea"
                                name="description"
                                value={editData?.description || ""}
                                onChange={handleEditChange}
                                rows={3}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Giá</Form.Label>
                            <Form.Control
                                type="number"
                                name="price"
                                value={editData?.price || ""}
                                onChange={handleEditChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Số slot còn lại</Form.Label>
                            <Form.Control
                                type="number"
                                name="availableSlot"
                                value={editData?.availableSlot || ""}
                                onChange={handleEditChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Địa chỉ</Form.Label>
                            <Form.Control
                                type="text"
                                name="address"
                                value={editData?.address || ""}
                                onChange={handleEditChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Loại dịch vụ</Form.Label>
                            <Form.Control
                                type="text"
                                name="serviceType"
                                value={editData?.serviceType || ""}
                                onChange={handleEditChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Ảnh (không chọn nếu giữ nguyên)</Form.Label>
                            <Form.Control
                                type="file"
                                name="image"
                                accept="image/*"
                                onChange={handleEditChange}
                            />
                        </Form.Group>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowEditModal(false)}>
                            Đóng
                        </Button>
                        <Button
                            type="submit"
                            variant="primary"
                            disabled={editLoading}
                        >
                            {editLoading ? "Đang lưu..." : "Lưu"}
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </Card>
    );
};

export default ServicePostDetail;