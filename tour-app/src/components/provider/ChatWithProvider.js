import React, { useState } from "react";
import ChatBox from "../user/ChatBox";

const ChatWithProvider = ({
  userId,
  providerId,
  userName,
  providerName,
  myId,
  avatarCurrentUser,
  onClose
}) => {
  return (
    <ChatBox
      userId={userId}
      providerId={providerId}
      userName={userName}
      providerName={providerName}
      myId={myId}
      role="USER"
      onClose={onClose}
      avatarCurrentUser={avatarCurrentUser}
    />
  );
};

export default ChatWithProvider;