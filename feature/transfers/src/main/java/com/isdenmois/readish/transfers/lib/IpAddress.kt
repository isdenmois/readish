package com.isdenmois.readish.transfers.lib

/**
 * `WifiManager.getConnectionInfo().ipAddress` is stored little-endian:
 * the first octet occupies the lowest byte.
 */
internal fun formatIpAddress(ipAddress: Int): String = "%d.%d.%d.%d".format(
    ipAddress and 0xff,
    ipAddress shr 8 and 0xff,
    ipAddress shr 16 and 0xff,
    ipAddress shr 24 and 0xff
)
