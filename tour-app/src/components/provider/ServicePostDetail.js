import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios, { endpoints, authApis } from "../configs/Apis";
import { MyUserContext } from "../configs/Context";
import { Card, Button, Spinner, Alert, Modal, Form } from "react-bootstrap";
import { toast } from "react-toastify";
import { CKEditor } from "@ckeditor/ckeditor5-react";
import CustomEditor from '../../ckeditor/build/ckeditor';
import { UploadAdapterPlugin } from "../layout/CustomUploadAdapter";
import cookie from "react-cookies";

const SERVICE_TYPE_OPTIONS = [
    "ROOM",
    "TOUR",
    "TRANSPORTATION"
];
const TRANSPORT_ENUMS = [
    { value: "BUS", label: "Xe Bus" },
    { value: "PLANE", label: "Máy bay" },
    { value: "SHIP", label: "Tàu thuỷ" }
];

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

    const isOwner =
        user &&
        user.role === "PROVIDER" &&
        user.provider &&
        post &&
        Number(user.provider.providerId) === Number(post.serviceProviderId);

    const handleEdit = () => {
        if (!post) return;
        setEditData({
            name: post.name,
            description: post.description,
            price: post.price,
            availableSlot: post.availableSlot,
            address: post.address,
            serviceType: post.serviceType,
            image: null,
            transportType: post.transportType || "",
            transportStartDate: post.transportStartDate
                ? new Date(post.transportStartDate).toISOString().slice(0, 16)
                : "",
            destination: post.destination || "",
            roomStartDate: post.roomStartDate
                ? new Date(post.roomStartDate).toISOString().slice(0, 16)
                : "",
            roomEndDate: post.roomEndDate
                ? new Date(post.roomEndDate).toISOString().slice(0, 16)
                : "",
            tourStartDate: post.tourStartDate
                ? new Date(post.tourStartDate).toISOString().slice(0, 16)
                : "",
            tourEndDate: post.tourEndDate
                ? new Date(post.tourEndDate).toISOString().slice(0, 16)
                : ""
        });
        setShowEditModal(true);
    };

    const handleEditSubmit = async (e) => {
        e.preventDefault();
        setEditLoading(true);
        try {
            const url = `${endpoints["service-post-edit"]}/${id}`;
            const formData = new FormData();
            formData.append("name", editData.name);
            formData.append("description", editData.description);
            formData.append("price", editData.price);
            formData.append("availableSlot", editData.availableSlot);
            formData.append("address", editData.address);
            formData.append("serviceType", editData.serviceType);
            if (editData.image) formData.append("image", editData.image);

            if (editData.serviceType === "TRANSPORTATION") {
                formData.append("transportType", editData.transportType);
                formData.append("transportStartDate", editData.transportStartDate);
                formData.append("destination", editData.destination);
            }
            if (editData.serviceType === "ROOM") {
                formData.append("roomStartDate", editData.roomStartDate);
                if (editData.roomEndDate) formData.append("roomEndDate", editData.roomEndDate);
            }
            if (editData.serviceType === "TOUR") {
                formData.append("tourStartDate", editData.tourStartDate);
                if (editData.tourEndDate) formData.append("tourEndDate", editData.tourEndDate);
            }

            const token = cookie.load("token");
            await authApis(token).put(url, formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });
            toast.success("Cập nhật thành công!");
            setShowEditModal(false);
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

    const handleCKEditorChange = (event, editor) => {
        const data = editor.getData();
        setEditData((prev) => ({
            ...prev,
            description: data
        }));
    };

    const handleDelete = async () => {
        if (!window.confirm("Bạn chắc chắn muốn xóa dịch vụ này?")) return;
        try {
            const token = cookie.load("token");
            console.log("Token khi xóa:", token);

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
        <Card className="shadow my-4 mx-auto" style={{ maxWidth: "600px" }}>
            {post.image && (
                <Card.Img
                    variant="top"
                    src={post.image}
                    style={{ objectFit: "cover", height: "220px" }}
                />
            )}
            <Card.Body>
                <Card.Title style={{ color: "#0d6efd", fontWeight: "bold", fontSize: "1.2rem" }}>
                    {post.companyName}
                </Card.Title>
                <Card.Title style={{ fontWeight: "bold", fontSize: "1.5rem" }}>
                    {post.name}
                </Card.Title>
                <Card.Text>
                    <span style={{
                        color: "#28a745",
                        fontWeight: "bold",
                        fontSize: "1.2rem",
                    }}>
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
                        <b>Mô tả:</b>
                    </span>
                    {/* Đổi từ <p> sang <div> để không vi phạm DOM nesting */}
                    <div className="service-desc-content" dangerouslySetInnerHTML={{ __html: post.description || "" }} />
                    <br />
                    <span>
                        <b>Ngày tạo:</b> {formatDate(post.createdDate)}
                    </span>
                    <br />
                    <span>
                        <b>Còn lại:</b> {post.availableSlot ?? 0} slot
                    </span>
                    {post.serviceType === "ROOM" && (
                        <>
                            <br />
                            <span>
                                <b>Ngày bắt đầu phòng:</b> {formatDate(post.roomStartDate)}
                            </span>
                            <br />
                            <span>
                                <b>Ngày kết thúc phòng:</b> {formatDate(post.roomEndDate)}
                            </span>
                        </>
                    )}
                    {post.serviceType === "TOUR" && (
                        <>
                            <br />
                            <span>
                                <b>Ngày bắt đầu tour:</b> {formatDate(post.tourStartDate)}
                            </span>
                            <br />
                            <span>
                                <b>Ngày kết thúc tour:</b> {formatDate(post.tourEndDate)}
                            </span>
                        </>
                    )}
                    {post.serviceType === "TRANSPORTATION" && (
                        <>
                            <br />
                            <span>
                                <b>Loại phương tiện:</b> {post.transportType}
                            </span>
                            <br />
                            <span>
                                <b>Ngày khởi hành:</b> {formatDate(post.transportStartDate)}
                            </span>
                            <br />
                            <span>
                                <b>Điểm đến:</b> {post.destination}
                            </span>
                        </>
                    )}
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
                            <CKEditor
                                editor={CustomEditor}
                                data={editData?.description || ""}
                                onReady={(editor) => {
                                    UploadAdapterPlugin(editor);
                                    editor.editing.view.change((writer) => {
                                        const root = editor.editing.view.document.getRoot();
                                        writer.setStyle("min-height", "250px", root);
                                        writer.setStyle("max-height", "400px", root);
                                        writer.setStyle("overflow-y", "auto", root);
                                        writer.setStyle("padding", "10px", root);
                                        writer.setStyle("border", "1px solid #ccc", root);
                                        writer.setStyle("border-radius", "6px", root);
                                    });
                                }}
                                onChange={handleCKEditorChange}
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
                            <Form.Select
                                name="serviceType"
                                value={editData?.serviceType || ""}
                                onChange={handleEditChange}
                                required
                                disabled
                            >
                                <option value="">-- Chọn loại dịch vụ --</option>
                                {SERVICE_TYPE_OPTIONS.map(type => (
                                    <option key={type} value={type}>{type}</option>
                                ))}
                            </Form.Select>
                        </Form.Group>
                        {editData?.serviceType === "TRANSPORTATION" && (
                            <>
                                <Form.Group className="mb-3">
                                    <Form.Label>Loại phương tiện</Form.Label>
                                    <Form.Select
                                        name="transportType"
                                        value={editData.transportType || ""}
                                        onChange={handleEditChange}
                                        required
                                    >
                                        <option value="">-- Chọn loại phương tiện --</option>
                                        {TRANSPORT_ENUMS.map(opt => (
                                            <option value={opt.value} key={opt.value}>{opt.label}</option>
                                        ))}
                                    </Form.Select>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Ngày khởi hành</Form.Label>
                                    <Form.Control
                                        type="datetime-local"
                                        name="transportStartDate"
                                        value={editData.transportStartDate || ""}
                                        onChange={handleEditChange}
                                        required
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Điểm đến</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="destination"
                                        value={editData.destination || ""}
                                        onChange={handleEditChange}
                                        required
                                    />
                                </Form.Group>
                            </>
                        )}
                        {editData?.serviceType === "ROOM" && (
                            <>
                                <Form.Group className="mb-3">
                                    <Form.Label>Ngày bắt đầu phòng</Form.Label>
                                    <Form.Control
                                        type="datetime-local"
                                        name="roomStartDate"
                                        value={editData.roomStartDate || ""}
                                        onChange={handleEditChange}
                                        required
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Ngày kết thúc phòng</Form.Label>
                                    <Form.Control
                                        type="datetime-local"
                                        name="roomEndDate"
                                        value={editData.roomEndDate || ""}
                                        onChange={handleEditChange}
                                    />
                                </Form.Group>
                            </>
                        )}
                        {editData?.serviceType === "TOUR" && (
                            <>
                                <Form.Group className="mb-3">
                                    <Form.Label>Ngày bắt đầu tour</Form.Label>
                                    <Form.Control
                                        type="datetime-local"
                                        name="tourStartDate"
                                        value={editData.tourStartDate || ""}
                                        onChange={handleEditChange}
                                        required
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Ngày kết thúc tour</Form.Label>
                                    <Form.Control
                                        type="datetime-local"
                                        name="tourEndDate"
                                        value={editData.tourEndDate || ""}
                                        onChange={handleEditChange}
                                    />
                                </Form.Group>
                            </>
                        )}
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