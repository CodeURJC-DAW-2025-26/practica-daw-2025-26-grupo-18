import { redirect } from "react-router";

import { getMyProfileId } from "~/services/profileService";
import { loadGlobalDataIntoStore } from "~/services/globalService";

export async function clientLoader() {
  const globalData = await loadGlobalDataIntoStore();
  if (!globalData?.isUserLoggedIn) {
    return redirect("/new/login");
  }

  try {
    const userId = await getMyProfileId();
    return redirect(`/new/profile/${userId}`);
  } catch {
    return redirect("/new/login");
  }
}

clientLoader.hydrate = true;

export default function ProfileMeRoute() {
  return null;
}
