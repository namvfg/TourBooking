import React, { useEffect, useState, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios, { endpoints } from "../configs/Apis";
import { Container, Row, Col, Card, Spinner, Alert } from "react-bootstrap";
import parse from "html-react-parser";
import { useParams, useNavigate } from "react-router-dom";
import { endpoints, authApis } from "../configs/Apis";
import { MyUserContext } from "../configs/Context";
import { Card, Button, Spinner, Alert, Modal, Form } from "react-bootstrap";
import { toast } from "react-toastify";
import { CKEditor } from "@ckeditor/ckeditor5-react";
import CustomEditor from '../../ckeditor/build/ckeditor';
import { UploadAdapterPlugin } from "../layout/CustomUploadAdapter";
import cookie from "react-cookies";
import Apis from "../configs/Apis";

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

    useEffect(() => {
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
            const res = await Apis.get(`${endpoints["service-post-detail"]}/${id}`);
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
