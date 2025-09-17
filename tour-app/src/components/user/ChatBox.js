import React, { useEffect, useState, useRef } from "react";
import { db } from "../configs/firebase";
import {
  collection,
  addDoc,
  query,
  orderBy,
  onSnapshot,
  serverTimestamp,
  doc,
  setDoc
} from "firebase/firestore";

// Thêm prop avatarCurrentUser cho lưu avatar người gửi
const ChatBox = ({
  userId,
  providerId,
  userName,
  providerName,
  myId,
  onClose,
  role,
  inMessenger,
  avatarCurrentUser
}) => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const messagesEndRef = useRef(null);

  const chatId = `${userId}_${providerId}`;

  useEffect(() => {
    const q = query(collection(db, "chats", chatId, "messages"), orderBy("timestamp"));
    const unsubscribe = onSnapshot(q, (querySnapshot) => {
      setMessages(querySnapshot.docs.map(doc => doc.data()));
      messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    });
    return () => unsubscribe();
  }, [chatId]);

  const sendMessage = async (e) => {
    e.preventDefault();
    if (input.trim() === "") return;
    // Lưu tên vào document chat
    await setDoc(doc(db, "chats", chatId), {
      userId,
      providerId,
      userName,
      providerName,
      lastUpdated: serverTimestamp()
    }, { merge: true });

    await addDoc(collection(db, "chats", chatId, "messages"), {
      senderId: myId,
      content: input,
      timestamp: serverTimestamp(),
      avatar: avatarCurrentUser || "/default-avatar.png", // avatar người gửi
    });
    setInput("");
  };

  // Tiêu đề động
  let title = "";
  if (role === "PROVIDER") {
    title = `Chat với ${userName || "khách hàng"}`;
  } else {
    title = `Chat với ${providerName || "nhà cung cấp"}`;
  }

  // Hàm format thời gian
  const formatTime = (ts) => {
    if (!ts) return "";
    try {
      let date;
      if (typeof ts === "object" && ts.seconds) {
        date = ts.toDate();
      } else {
        date = new Date(ts);
      }
      return date.toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" });
    } catch {
      return "";
    }
  };

  return (
    <div style={{
      height: "100%",
      background: "#fff",
      border: "1px solid #ccc",
      boxShadow: "0 2px 10px rgba(0,0,0,0.2)",
      borderRadius: 10,
      display: "flex",
      flexDirection: "column"
    }}>
      <div style={{ padding: 10, borderBottom: "1px solid #ddd", background: "#f6f6f6" }}>
        <strong>{title}</strong>
        {!inMessenger && <button style={{ float: "right" }} onClick={onClose}>&times;</button>}
      </div>
      <div style={{ flex: 1, overflowY: "auto", padding: 10, maxHeight: 400 }}>
        {messages.map((msg, idx) => {
          const isMine = msg.senderId === myId;
          return (
            <div key={idx} style={{
              display: "flex",
              flexDirection: isMine ? "row-reverse" : "row",
              alignItems: "flex-end",
              marginBottom: 8
            }}>
              <img
                src={msg.avatar || "/default-avatar.png"}
                alt="avatar"
                style={{
                  width: 32, height: 32, borderRadius: "50%",
                  marginLeft: isMine ? 8 : 0,
                  marginRight: isMine ? 0 : 8,
                  background: "#eee",
                  objectFit: "cover"
                }}
              />
              <div>
                <div style={{
                  background: isMine ? "#dcf8c6" : "#f1f1f1",
                  borderRadius: 8,
                  padding: "6px 12px",
                  margin: "2px 0",
                  maxWidth: 220,
                  wordBreak: "break-word",
                  display: "inline-block"
                }}>
                  {msg.content}
                </div>
                <div style={{
                  fontSize: 12,
                  color: "#999",
                  textAlign: isMine ? "right" : "left",
                  marginTop: 2
                }}>
                  {formatTime(msg.timestamp)}
                </div>
              </div>
            </div>
          )
        })}
        <div ref={messagesEndRef} />
      </div>
      <form onSubmit={sendMessage} style={{ display: "flex", borderTop: "1px solid #eee" }}>
        <input
          value={input}
          onChange={e => setInput(e.target.value)}
          placeholder="Nhập tin nhắn..."
          style={{ flex: 1, border: "none", padding: 10, outline: "none" }}
        />
        <button type="submit" style={{ border: "none", background: "#007bff", color: "#fff", padding: "0 16px" }}>Gửi</button>
      </form>
    </div>
  );
};

export default ChatBox;