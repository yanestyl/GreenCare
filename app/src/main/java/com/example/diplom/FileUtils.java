import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {

    public static String getPathFromUri(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return filePath;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(sourceFile);
             FileChannel sourceChannel = inputStream.getChannel();
             FileOutputStream outputStream = new FileOutputStream(destFile);
             FileChannel destChannel = outputStream.getChannel()) {

            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        }
    }
}
