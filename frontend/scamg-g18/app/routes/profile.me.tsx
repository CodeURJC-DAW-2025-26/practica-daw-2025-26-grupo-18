import { redirect } from "react-router";

import { getMyProfileId } from "~/services/profileService";

export async function loader() {
  try {
    const userId = await getMyProfileId();
    return redirect(`/new/profile/${userId}`);
  } catch {
    return redirect("/new/login");
  }
}

export default function ProfileMeRoute() {
  return null;
}
