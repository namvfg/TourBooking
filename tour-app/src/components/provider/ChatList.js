import React, { useEffect, useState } from "react";
import { db } from "../configs/firebase";
import { collection, onSnapshot } from "firebase/firestore";

// mode: "user" hoặc "provider", currentId: id của user hoặc provider hiện tại
const ChatList = ({ mode, currentId, currentUser, onSelectChat, selectedChat }) => {
  const [chats, setChats] = useState([]);

  useEffect(() => {
    const chatsRef = collection(db, "chats");
    const unsubscribe = onSnapshot(chatsRef, (snapshot) => {
      const result = [];
      snapshot.forEach(docSnap => {
        const chatId = docSnap.id;
        const [userId, providerId] = chatId.split("_");
        const data = docSnap.data();
        if (
          (mode === "user" && userId === String(currentId)) ||
          (mode === "provider" && providerId === String(currentId))
        ) {
          result.push({
            chatId,
            userId,
            providerId,
            userName: data.userName,
            providerName: data.providerName
          });
        }
      });
      setChats(result);
    });
    return unsubscribe;
  }, [mode, currentId]);

  return (
    <div>
      <h5 style={{ padding: 10 }}>{mode === "provider" ? "Khách đã nhắn" : "Nhà cung cấp đã nhắn"}</h5>
      {chats.map(chat => (
        <div
          key={chat.chatId}
          style={{
            padding: 10,
            cursor: "pointer",
            background: selectedChat && selectedChat.chatId === chat.chatId ? "#f6f6f6" : ""
          }}
          onClick={() => onSelectChat(chat)}
        >
          {mode === "provider"
            ? (chat.userName || `User ${chat.userId}`)
            : (chat.providerName || `Provider ${chat.providerId}`)}
        </div>
      ))}
    </div>
  );
};

export default ChatList;